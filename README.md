# 📰 My News - Personalized News Aggregator

**My News** is a smart web application that aggregates news from various global sources and tailors the content to each user's specific interests. Users can create accounts, select their preferred sources (e.g., CNN, BBC, etc.), and enjoy a fully personalized news feed.

---

## 🚀 Technical Highlights

### 🛠 Tech Stack

- **Frontend:** React (powered by Vite)
- **Backend:** Spring Boot (Java)
- **Database:** MySQL
- **Authentication:** SSO (Google & Microsoft OAuth2)
- **Data Sourcing:** External News API every hour

### 🕒 High-Performance News Scheduler

The core of the application is an automated data ingestion engine designed for efficiency:
- **Scheduled Ingestion:** A background scheduler runs every hour to fetch the latest articles.
- **Multithreading & Batching:** To handle 80+ news sources efficiently, the system splits sources into **batches**. It utilizes **parallel threads** to fetch data concurrently, significantly reducing the total synchronization time.
- **Data Integrity:** The backend ensures no duplicate articles are stored, maintaining a clean and optimized MySQL database.

### 🔐 Secure Authentication

- **Single Sign-On (SSO):** Integration with Google and Microsoft for a seamless and secure login experience.
- **Personalized Profiles:** User source selections are securely stored and mapped to their unique profiles.

---

## 🔍 Features

### Authentication

Users can sign in securely via Google or Microsoft

<img width="1917" height="722" alt="image" src="https://github.com/user-attachments/assets/39b8253b-e4ce-44b0-b6fe-6188987aed45" />

### Source Management

An intuitive interface to browse and select from 80+ different global news providers.

<img width="1913" height="909" alt="image" src="https://github.com/user-attachments/assets/816ce492-01ca-49fd-877b-50597b3c26a6" />

### Personalized News Feed

A clean, card-based UI where users see news only from the sources they have subscribed to.

<img width="1898" height="896" alt="image" src="https://github.com/user-attachments/assets/b8f337b7-8900-477d-abc4-e20d5c9dc38f" />

---







