package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.entity.enums.*;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.service.MlmScheduledServices;
import com.metuncc.mlm.utils.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class MlmScheduledServicesImpl implements MlmScheduledServices {
    @Value("${default.start.hour}")
    private String startHour;

    @Value("${default.end.hour}")
    private String endHour;

    @Value("${default.borrow.day:10}")
    private Long day;
    @Value("${default.borrow.late.debt:2}")
    private Long lateDebt;


    private LocalDateTime lastExecutionTime;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ShelfRepository shelfRepository;
    private RoomRepository roomRepository;
    private ImageRepository imageRepository;
    private BookRepository bookRepository;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private MailUtil mailUtil;
    private VerificationCodeRepository verificationCodeRepository;
    private BookBorrowHistoryRepository bookBorrowHistoryRepository;
    private BookQueueRecordRepository bookQueueRecordRepository;
    private CopyCardRepository copyCardRepository;
    private RoomSlotRepository roomSlotRepository;
    private RoomReservationRepository roomReservationRepository;
    private MlmServicesImpl mlmServices;
    private BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository;
    private StatisticsRepository statisticsRepository;
    private EmailRepository emailRepository;

    public MlmScheduledServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ShelfRepository shelfRepository, RoomRepository roomRepository, ImageRepository imageRepository, BookRepository bookRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, MailUtil mailUtil, VerificationCodeRepository verificationCodeRepository, BookBorrowHistoryRepository bookBorrowHistoryRepository, BookQueueRecordRepository bookQueueRecordRepository, CopyCardRepository copyCardRepository, RoomSlotRepository roomSlotRepository, RoomReservationRepository roomReservationRepository, MlmServicesImpl mlmServices, BookQueueHoldHistoryRecordRepository bookQueueHoldHistoryRecordRepository, StatisticsRepository statisticsRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.shelfRepository = shelfRepository;
        this.roomRepository = roomRepository;
        this.imageRepository = imageRepository;
        this.bookRepository = bookRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailUtil = mailUtil;
        this.verificationCodeRepository = verificationCodeRepository;
        this.bookBorrowHistoryRepository = bookBorrowHistoryRepository;
        this.bookQueueRecordRepository = bookQueueRecordRepository;
        this.copyCardRepository = copyCardRepository;
        this.roomSlotRepository = roomSlotRepository;
        this.roomReservationRepository = roomReservationRepository;
        this.mlmServices = mlmServices;
        this.bookQueueHoldHistoryRecordRepository = bookQueueHoldHistoryRecordRepository;
        this.statisticsRepository = statisticsRepository;
        this.emailRepository = emailRepository;
    }

    //Every Hour with minute 1.
    @Scheduled(cron ="0 1 * * * *")
    @Override
    public void cancelPastHourSlot(){
        int currentDay = LocalDateTime.now().getDayOfWeek().getValue();
        int previousHour = LocalTime.now().minusHours(1).getHour();

        LocalTime previousSlotTime = LocalTime.of(previousHour, 0);
        List<RoomSlot> previousHourSlots = roomSlotRepository.getRoomSlotsByTimeAndDay(previousSlotTime, RoomSlotDays.fromValue(currentDay));

        for (RoomSlot slot : previousHourSlots){
            slot.setAvailable(false);
            roomSlotRepository.save(slot);
        }
    }

    //Every hour with minute 15.
    @Scheduled(cron = "0 15 * * * *")
    @Override
    public void cancelUnconfirmedReservations() {
        int day = LocalDateTime.now().getDayOfWeek().getValue();
        LocalTime localTime = LocalTime.now().withMinute(0).withSecond(0).withNano(0);
        List<RoomSlot> roomSlotList = roomSlotRepository.getRoomSlotsByTimeAndDay(localTime, RoomSlotDays.fromValue(day));
        List<RoomReservation> roomReservations = roomReservationRepository.getUnapprovedRoomReservationByRoomSlotList(roomSlotList);
        for (RoomReservation roomReservation : roomReservations) {
            try {
                roomReservation.getRoomSlot().setAvailable(true);
                roomSlotRepository.save(roomReservation.getRoomSlot());
                User user = userRepository.getById(roomReservation.getUserId());
                emailRepository.save(new Email().set(user.getEmail(),
                        "Your room reservation has been canceled \uD83D\uDE14 ",
                        "Since 15 minutes have passed since your reservation time and you have not made a reservation, your reservation has been cancelled.",
                        "Reservation has been canceled"));
            } catch (Exception e) {
                //Do nothing.
            }
        }
    }

    //Every day, at 00.05
    @Scheduled(cron = "0 5 0 * * *")
    @Override
    public void updateRoomSlots() {
        LocalDateTime yesterady = LocalDateTime.now().minusMinutes(100L);
        RoomSlotDays previousDay = RoomSlotDays.fromValue(yesterady.getDayOfWeek().getValue());
        List<RoomSlot> roomSlotList = roomSlotRepository.getRoomSlotsByDay(previousDay);
        List<RoomReservation> roomReservations = roomReservationRepository.findAllByRoomSlotList(roomSlotList);
        roomReservationRepository.deleteAll(roomReservations);
        roomSlotRepository.deleteAll(roomSlotList);

        LocalDateTime today = LocalDateTime.now();
        RoomSlotDays todayEnum = RoomSlotDays.fromValue(today.getDayOfWeek().getValue());
        List<RoomSlot> roomSlotListToday = roomSlotRepository.getRoomSlotsByDay(todayEnum);
        if (CollectionUtils.isEmpty(roomSlotListToday)) {
            mlmServices.createSlots(todayEnum, startHour, endHour);
        }

        LocalDateTime oneDayLater = LocalDateTime.now();
        RoomSlotDays oneDayLaterEnum = RoomSlotDays.fromValue(oneDayLater.getDayOfWeek().getValue());
        List<RoomSlot> roomSlotListForTomorrow = roomSlotRepository.getRoomSlotsByDay(oneDayLaterEnum);
        if (CollectionUtils.isEmpty(roomSlotListForTomorrow)) {
            mlmServices.createSlots(oneDayLaterEnum, startHour, endHour);
        }

        LocalDateTime twoDaysLater = LocalDateTime.now().plusDays(2L);
        RoomSlotDays twoDayLater = RoomSlotDays.fromValue(twoDaysLater.getDayOfWeek().getValue());
        List<RoomSlot> twoDayLaterList = roomSlotRepository.getRoomSlotsByDay(twoDayLater);
        if(CollectionUtils.isEmpty(twoDayLaterList)){
            mlmServices.createSlots(twoDayLater, startHour, endHour);
        }



    }

    //Every day, at 23.45
    @Scheduled(cron = "0 45 23 * * ?")
    @Override
    public void dequeueForBooks(){
        LocalDateTime localDateTime = LocalDateTime.now();
        List<BookQueueHoldHistoryRecord> bookQueueHoldHistoryRecords= bookQueueHoldHistoryRecordRepository.getBookQueueHoldHistoryRecordByEndDate(localDateTime);
        for (BookQueueHoldHistoryRecord bookQueueHoldHistoryRecord : bookQueueHoldHistoryRecords) {
            BookQueueRecord bookQueueRecord = bookQueueHoldHistoryRecord.getBookQueueRecord();
            for (BookBorrowHistory bookBorrowHistory : bookQueueRecord.getBookBorrowHistoryList()) {
                if(bookBorrowHistory.getUserId().getId().equals(bookQueueHoldHistoryRecord.getUserId())){
                    bookBorrowHistory.setStatus(BorrowStatus.DID_NOT_TAKEN);
                }
            }
            List<BookBorrowHistory> restOfUsers = bookQueueRecord.getBookBorrowHistoryList().stream().filter(c -> c.getStatus().equals(BorrowStatus.WAITING_TAKE)).collect(Collectors.toList());
            //Next person.
            restOfUsers.sort(Comparator.comparing(BookBorrowHistory::getCreatedDate));
            BookQueueHoldHistoryRecord newbookQueueHoldHistoryRecord = new BookQueueHoldHistoryRecord();
            bookQueueHoldHistoryRecord.setBookQueueRecord(bookQueueRecord);
            bookQueueHoldHistoryRecord.setUserId(restOfUsers.get(0).getUserId().getId());
            bookQueueHoldHistoryRecord.setEndDate(LocalDateTime.now().plusDays(1L).withHour(23).withMinute(30).withSecond(0).withNano(0));
            bookQueueHoldHistoryRecordRepository.save(newbookQueueHoldHistoryRecord);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            emailRepository.save(new Email().set(restOfUsers.get(0).getUserId().getEmail(),
                    "The Book is Available \uD83C\uDF89 ",
                    bookQueueRecord.getBookId().getName()+" is available now! We keep the book for you for a day. We would like to remind you that if you do not take the book by "+ bookQueueHoldHistoryRecord.getEndDate().format(formatter)+", the book will be reserved for the next person in line.",
                    "The Book is Available!"));

        }
        bookQueueHoldHistoryRecordRepository.deleteAll(bookQueueHoldHistoryRecords);
    }
    //Every day, at 23.45
    @Scheduled(cron = "0 41 11 * * ?")
    @Override
    public void increaseDebt(){
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(day+1);
        List<BookBorrowHistory> bookBorrowHistoryList = bookBorrowHistoryRepository.getBookBorrowHistoriesByStatusAndDate(localDateTime,BorrowStatus.WAITING_RETURN);
        for (BookBorrowHistory bookBorrowHistory : bookBorrowHistoryList) {
            if(Objects.isNull(bookBorrowHistory.getUserId().getDebt())){
                bookBorrowHistory.getUserId().setDebt(BigDecimal.valueOf(lateDebt));
            }else{
                bookBorrowHistory.getUserId().setDebt(bookBorrowHistory.getUserId().getDebt().add(BigDecimal.valueOf(lateDebt)));
            }
            userRepository.save(bookBorrowHistory.getUserId());
            emailRepository.save(new Email().set(
                    bookBorrowHistory.getUserId().getEmail(),
                    "Reminder for "+bookBorrowHistory.getBookQueueRecord().getBookId().getName(),
                    "We saw that you did not return "+bookBorrowHistory.getBookQueueRecord().getBookId().getName()+". You must return the book to the library to avoid penalties.",
                    "Remainder"
            ));
        }
    }
    //Every day, at 23.45
    @Scheduled(cron = "0 01 00 * * ?")
    @Override
    public void logStatistics(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DayOfWeek now = localDateTime.getDayOfWeek();
        List<Statistics> statistics = statisticsRepository.findAll();
        Statistics today = new Statistics();
        today.setTotalUserCount(userRepository.totalUserCount());
        today.setTotalBookCount(bookRepository.totalBookCount());
        today.setAvailableBookCount(bookRepository.bookCountByAvailability(BookStatus.AVAILABLE));
        today.setUnavailableBookCount(bookRepository.bookCountByAvailability(BookStatus.NOT_AVAILABLE));
        today.setSumOfBalance(copyCardRepository.totalBalance());
        today.setSumOfDebt(userRepository.totalDebt());
        today.setQueueCount(bookQueueRecordRepository.getBookQueueRecordByStatus(QueueStatus.ACTIVE));
        today.setDay(now);
        today = statisticsRepository.save(today);
        statistics.add(today);
        if(statistics.size()>7){
            Collections.sort(statistics, Comparator.comparingLong(Statistics::getId));
            statisticsRepository.delete(statistics.get(0));
        }
    }

    //Every min.
    @Scheduled(fixedRate = 60000)
    public void sendScheduledEmails(){
        List<Email> emails = emailRepository.findAllEmailsByStatus(EmailStatus.SCHEDULED);
        if(emails.size()>5){
            emails = emails.subList(0,4);
        }
        emailRepository.updateAllStatus(emails);
        for (Email email : emails) {
            email.setLastTryDate(LocalDateTime.now());
            email.setTryCount(Objects.nonNull(email.getTryCount())?email.getTryCount()+1:1L);
            emailRepository.save(email);
            try{
                mailUtil.sendCustomEmail(
                        email.getToEmail(),
                        email.getSubject(),
                        email.getContent(),
                        email.getTitle()
                );
                email.setEmailStatus(EmailStatus.COMPLETED);
                emailRepository.save(email);
            }catch (Exception e){
                email.setEmailStatus(EmailStatus.SCHEDULED);
                emailRepository.save(email);
            }
        }
    }

    @Scheduled(fixedRate = 900000)
    public void deleteVerificationCodes(){
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(15L);
        List<VerificationCode> verificationCodes = verificationCodeRepository.getVerificationCodesByCreatedDateBefore(localDateTime,VerificationType.RESET_PASSWORD);
        verificationCodeRepository.deleteAll(verificationCodes);
    }
}


