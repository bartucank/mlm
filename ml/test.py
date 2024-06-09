import random
import unittest
import pandas as pd
from main import preprocess_data, create_book_pivot, train_model, get_recommendations_for_book
from main import precision_at_k, recall_at_k, mean_average_precision

import requests

class TestRecommendationSystem(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        print("Starting tests...")
#         cls.download_csv('https://eew.com.tr/srv/api/external/csv/reviews', 'test_reviews.csv')
#         cls.download_csv('https://eew.com.tr/srv/api/external/csv/books', 'test_books.csv')

        cls.reviews = pd.read_csv('test_reviews.csv')
        cls.books = pd.read_csv('test_books.csv')
        cls.default_book_id = "4"  # Default book ID for testing

        cls.merged_data = preprocess_data(cls.reviews, cls.books)
        cls.book_pivot = create_book_pivot(cls.merged_data)
        cls.model = train_model(cls.book_pivot)

        cls.relevant_items = [9, 14, 16, 6, 5, 3]

    @classmethod
    def tearDownClass(cls):
        print("Tests finished.")

#     @staticmethod
#     def download_csv(url, filename):
#         response = requests.get(url)
#         if response.status_code == 200:
#             with open(filename, 'wb') as file:
#                 file.write(response.content)
#                 print(f"CSV file downloaded successfully: {filename}")
#         else:
#             print("Error occurred while downloading CSV file:", response.text)

    def test_get_recommendations_for_book(self):
        print("Running test_get_recommendations_for_book...")
        book_id = self.default_book_id
        recommendations = get_recommendations_for_book(book_id, self.model, self.book_pivot)
        self.assertIsInstance(recommendations, list)
        self.assertGreaterEqual(6, len(recommendations))
        print("Recommendations:", recommendations)
        if len(recommendations) >= 6:
            print("Test successful.")
        else:
            print("Test failed.")

    def test_precision_at_k(self):
        print("Running test_precision_at_k...")
        book_id = self.default_book_id
        recommendations = get_recommendations_for_book(book_id, self.model, self.book_pivot)
        print("Relevant items:", self.relevant_items)
        precision = precision_at_k(recommendations, self.relevant_items, k=6)  # Use k=6 for top 6 recommendations
        print("Precision:", precision)
        self.assertGreaterEqual(precision, 0)
        self.assertLessEqual(precision, 1)
        if 0 <= precision <= 1:
            print("Test successful.")
        else:
            print("Test failed.")

    def test_recall_at_k(self):
        print("Running test_recall_at_k...")
        book_id = self.default_book_id
        recommendations = get_recommendations_for_book(book_id, self.model, self.book_pivot)
        print("Relevant items:", self.relevant_items)
        recall = recall_at_k(recommendations, self.relevant_items, k=6)
        print("Recall:", recall)
        self.assertGreaterEqual(recall, 0)
        self.assertLessEqual(recall, 1)
        if 0 <= recall <= 1:
            print("Test successful.")
        else:
            print("Test failed.")

    def test_mean_average_precision(self):
        print("Running test_mean_average_precision...")
        book_id = self.default_book_id
        recommendations = get_recommendations_for_book(book_id, self.model, self.book_pivot)
        print("Relevant items:", self.relevant_items)
        map_score = mean_average_precision(recommendations, self.relevant_items)
        print("MAP:", map_score)
        self.assertGreaterEqual(map_score, 0)
        self.assertLessEqual(map_score, 1)
        if 0 <= map_score <= 1:
            print("Test successful.")
        else:
            print("Test failed.")

if __name__ == '__main__':
    unittest.main()
