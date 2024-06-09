import subprocess

# libraries_to_install = ["requests", "flask", "scipy", "scikit-learn", "flask_cors","json","pandas","numpy","matplotlib.pyplot","seaborn"]
# for library in libraries_to_install:
#     try:
#         subprocess.run(["pip3", "install", library], check=False)
#         print(f"{library} installed")
#     except subprocess.CalledProcessError as e:
#         print(f"Error !!! library: {library}  error: {e}")
from datetime import datetime, timedelta
import joblib
import os
import requests
import json
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from flask import Flask, request, jsonify
import logging
from flask_cors import CORS
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors



app = Flask(__name__)
CORS(app)

def mean_average_precision(recommended_items, relevant_items):
    relevant_set = set(relevant_items)
    score = 0.0
    num_hits = 0.0
    for i, item in enumerate(recommended_items):
        if item in relevant_set:
            num_hits += 1.0
            score += num_hits / (i + 1.0)
    return score / max(1.0, len(relevant_set))

def precision_at_k(recommended_items, relevant_items, k):
    recommended_k = recommended_items[:k]
    relevant_set = set(relevant_items)
    recommended_set = set(recommended_k)
    return len(recommended_set & relevant_set) / k

def recall_at_k(recommended_items, relevant_items, k):
    recommended_k = recommended_items[:k]
    relevant_set = set(relevant_items)
    recommended_set = set(recommended_k)
    return len(recommended_set & relevant_set) / len(relevant_set)

def download_csv(url, filename):
    response = requests.get(url)
    if response.status_code == 200:
        with open(filename, 'wb') as file:
            file.write(response.content)
            print("CSV file downloaded successfully")
    else:
        print("Error occurred:", response.text)

def check_csv_is_too_old():
    if not os.path.exists('books.csv') or not os.path.exists('reviews.csv'):
        download_csv('https://eew.com.tr/srv/api/external/csv/reviews', 'reviews.csv')
        download_csv('https://eew.com.tr/srv/api/external/csv/books', 'books.csv')
    else:
        model_modification_time = datetime.fromtimestamp(os.path.getmtime('books.csv'))
        current_time = datetime.now()
        if current_time - model_modification_time > timedelta(hours=3):
            download_csv('https://eew.com.tr/srv/api/external/csv/reviews', 'reviews.csv')
            download_csv('https://eew.com.tr/srv/api/external/csv/books', 'books.csv')

def startRecommend(bookId=None, userId=None, mod=None):
    if mod == "book":
        return book_based_recommendation(bookId)
    elif mod == "user":
        return user_based_recommendation(userId)
    else:
        return {"error": "Invalid mod parameter. Please specify 'book' or 'user'."}

def book_based_recommendation(bookId):
    check_csv_is_too_old()
    reviews = pd.read_csv('reviews.csv')
    books = pd.read_csv('books.csv')

    merged_data = preprocess_data(reviews, books)

    book_pivot = create_book_pivot(merged_data)

    model = train_model(book_pivot)

    recommendations = get_recommendations_for_book(bookId, model, book_pivot)

    return recommendations

def user_based_recommendation(userId):
    check_csv_is_too_old()
    reviews = pd.read_csv('reviews.csv')
    books = pd.read_csv('books.csv')

    merged_data = preprocess_data(reviews, books)

    model = train_user_based_model(merged_data)

    recommendations = get_recommendations_for_user(userId, model, merged_data)

    return recommendations

def preprocess_data(reviews, books):
    merged_data = reviews.merge(books, on='isbn')
    return merged_data

def create_book_pivot(merged_data):
    book_pivot = merged_data.pivot_table(columns='userid', index='bookid', values='rate')
    book_pivot.fillna(0, inplace=True)
    book_pivot.to_csv('book_pivot.csv')
    return book_pivot

def train_model(book_pivot):
    book_sparse = csr_matrix(book_pivot)
    model = NearestNeighbors(algorithm='brute')
    model.fit(book_sparse)
    return model

def train_user_based_model(merged_data):
    user_item_matrix = merged_data.pivot_table(index='userid', columns='bookid', values='rate', fill_value=0)
    model = NearestNeighbors(metric='cosine', algorithm='brute')
    model.fit(user_item_matrix)
    return model

def get_recommendations_for_book(bookId, model, book_pivot):
    distance, suggestion = model.kneighbors(book_pivot.iloc[[bookId]].values, n_neighbors=6)
    recommendations = []
    for i in range(len(suggestion)):
        recommendations.append(book_pivot.index[suggestion[i]])
    return [int(book_id) for book_id in recommendations[0]]

def get_recommendations_for_user(userId, model, merged_data):
    user_item_matrix = merged_data.pivot_table(index='userid', columns='bookid', values='rate', fill_value=0)
    user_index = user_item_matrix.index.get_loc(userId)
    distances, indices = model.kneighbors(user_item_matrix.iloc[user_index].values.reshape(1, -1), n_neighbors=6)
    similar_users = user_item_matrix.iloc[indices[0][1:]]
    recommended_books = similar_users.mean(axis=0).sort_values(ascending=False).index
    return recommended_books.tolist()

@app.route('/getRecommend', methods=['GET'])
def get_recommendation():
    if 'mod' in request.args:
        mod = request.args['mod']
        if mod == "book":
            if 'bookId' in request.args:
                book_id = int(request.args['bookId'])
                recommendations = startRecommend(bookId=book_id, mod="book")
                return jsonify({"recommendations": recommendations})
            else:
                return jsonify({"error": "You must specify 'bookId' as parameter for book mode."})
        elif mod == "user":
            if 'userId' in request.args:
                user_id = int(request.args['userId'])
                recommendations = startRecommend(userId=user_id, mod="user")
                return jsonify({"recommendations": recommendations})
            else:
                return jsonify({"error": "You must specify 'userId' as parameter for user mode."})
        else:
            return jsonify({"error": "Invalid mod parameter. Please specify 'book' or 'user'."})
    else:
        return jsonify({"error": "You must specify 'mod' as parameter."})

if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=9010)
