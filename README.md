<br/>
<p align="center">
  <a href="https://github.com/bartucank/mlm">
    <img src="https://www.metu.edu.tr/system/files/logo_orj/3/3.8.png" alt="Logo" >
  </a>
  <h3 align="center">METU NCC Library Management</h3>

  <p align="center">
    Graduate project, Library Management System for METU NCC
    <br/>
    <br/>
  </p>
</p>
<center>

  
[![Build & Test & Report & Deploy](https://github.com/bartucank/mlm/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/bartucank/mlm/actions/workflows/gradle.yml)

  </center>


![Downloads](https://img.shields.io/github/downloads/bartucank/mlm/total) ![Contributors](https://img.shields.io/github/contributors/bartucank/mlm?color=dark-green) ![Issues](https://img.shields.io/github/issues/bartucank/mlm) ![License](https://img.shields.io/github/license/bartucank/mlm) 

## Table Of Contents

* [About the Project](#scope)
* [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Contributing](#contributing)
* [License](#license)
* [Authors](#authors)

## Scope

This project aims to combine different kinds of functionalities such as the copy card system, study
room reservation system, and borrowing and lending books system. The purpose of this project is
to create a more reliable, fast, and user-friendly library system application that encourages the
users to use this system very easily. In addition, thanks to new development strategies, queuing
and system databases will become very fast and also more secure because lending books is the
project's main goal for this part so it should be fast and more reliable. To achieve the aim, this
project has the following objectives:
  • Allow the user to borrow a book by managing a queue for each book.
  • Allow the user to make a reservation for a study room.
  • Allow the user to pay their debts to the library and deposit money to their copy cards by
    sharing the receipts.

## Built With

Back-end side of this project built with Java. Also, we used Spring Boot framework. Front-end side of this project built with Flutter.

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/bartucank/mlm.git
   ```

2. Change SMTP server information, DB information, secret key for authentication in bootstrap.yml file.
3. If you have a certificate, put it in the directory where the bootstrap.yml file is located, and edit following code given below in the bootstrap.yml file. <b>(If not please see step 4)</b>
   ```sh
         server.ssl.key-store=classpath:<certificate_name>.p12
         server.ssl.key-store-password=<password>
         server.ssl.keyStoreType= PKCS12
   ```
4. <b>(If you have a certificate, bypass this step!)</b> Please comment the code given below in the bootstrap.yml file.
    ```sh
         server.ssl.key-store=classpath:<certificate_name>.p12
         server.ssl.key-store-password=<password>
         server.ssl.keyStoreType= PKCS12
     ```

5. To build the project execute the following command:
   ```sh
   ./gradlew build
   ```
6. Run the project!
7. Clone Frontend side of the project, then follow the instructions on the readme!
   ```sh
   https://github.com/bartucank/mlmui
   ```
   

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.
* If you have suggestions for adding or removing projects, feel free to [open an issue](https://github.com/bartucank/mlm/issues/new) to discuss it, or directly create a pull request after you edit the *README.md* file with necessary changes.
* Please make sure you check your spelling and grammar.
* Create individual PR for each suggestion.
* Please also read through the [Code Of Conduct](https://github.com/bartucank/mlm/blob/main/CODE_OF_CONDUCT.md) before posting your first idea as well.

### Creating A Pull Request

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See [LICENSE](https://github.com/bartucank/mlm/blob/main/LICENSE.md) for more information.

## Authors

* **Bartu Can PALAUT** - *Computer Engineering Student* - [Bartu Can PALAMUT](https://github.com/bartucank/) 
* **Doğukan AKDAĞ** - *Computer Engineering Student* - [Doğukan AKDAĞ](https://github.com/dokann/)
* **Eren ÖZTÜRK** - *Computer Engineering Student* - [Eren ÖZTÜRK](https://github.com/ozturkeren/)
* **Ataberk Türk ATAĞ** - *Computer Engineering Student* - [Ataberk Türk ATAĞ](https://github.com/ataberkatag/) 



