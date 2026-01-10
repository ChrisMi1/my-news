import { useEffect, useState, useRef } from "react";
import "./NewsPage.css";
import { acquireToken } from "../authUtils";
import { useMsal } from "@azure/msal-react";
import { useAuth } from "../AuthFlow";
import { useNavigate } from "react-router-dom";

function NewsPage({ onLogout, onEditSources }) {
  const [articles, setArticles] = useState([]);
  const [pageNumber, setPageNumber] = useState(0);
  const [userSources, setUserSources] = useState([]);
  const [selectedSource, setSelectedSource] = useState("all");
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const carouselRef = useRef(null);
  const { instance, accounts } = useMsal();
  const { token, logout, refreshToken } = useAuth();
  const [currentToken, setCurrentToken] = useState(token);
  const navigate = useNavigate();
  
  const fetchUserSources = async () => {
    try {
      const res = await fetch("http://localhost:8080/mynews/api/users/sources", {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) {
        const data = await res.json();
        setUserSources(data);
      }
    } catch (err) {
      console.error("Failed to fetch sources", err);
    }
  };

  const fetchArticles = async (page = 0,source = "all") => {
    try {
      setLoading(true);
      setError(null);

      const bodyData = JSON.stringify({
          searchCriteria: source !== "all" ? { sourceName: source } : {},
          pageNumber: page,
          pageTotalContentSize: 6,
        });
      let res = await fetch(
        "http://localhost:8080/mynews/api/articles/search",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${currentToken}`,
          },
          body: bodyData,
        }
      );

      if (res.status === 401) {
      const newToken = await refreshToken();

      if (newToken) {
        
        res = await fetch("http://localhost:8080/mynews/api/articles/search", {
          method: "POST",
          headers: { 
              "Content-Type": "application/json", 
              "Authorization": `Bearer ${newToken}` 
          },
          body: bodyData
        });
      }
    }

      if (!res.ok) throw new Error();

      const data = await res.json();
      setArticles(data.content || []);
      setTotalPages(data.totalPages || 0);
      //setPageNumber(data.pageNumber);
      
    } catch {
      setError("Could not load news");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserSources();
  }, []);

  useEffect(() => {
    fetchArticles(pageNumber, selectedSource);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, [pageNumber, selectedSource]);



    const handleNext = () => setPageNumber(prev => prev + 1);
    const handlePrev = () => setPageNumber(prev => Math.max(prev - 1, 0));
    const handleSourceChange = (sourceName) => {
      setSelectedSource(sourceName);
      setPageNumber(0); 
    };

    const scrollCarousel = (direction) => {
    if (carouselRef.current) {
      const scrollAmount = 200;
      carouselRef.current.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth'
      });
    }
  };

  return (
    <div className="news-page-container">
      <header className="app-header">
        <div className="header-left">
          <span className="app-logo">📰 My News</span>
        </div>

        <div className="header-center">
          <div className="carousel-container">
            
            {userSources.length > 5 && (
              <button className="carousel-arrow" onClick={() => scrollCarousel("left")}>
                ❮
              </button>
            )}

            <div className="sources-carousel" ref={carouselRef}>
              <button
                className={`source-tag ${selectedSource === "all" ? "active" : ""}`}
                onClick={() => handleSourceChange("all")}
              >
                All
              </button>
              {userSources.map((source) => (
                <button
                  key={source.id}
                  className={`source-tag ${selectedSource === source.name ? "active" : ""}`}
                  onClick={() => handleSourceChange(source.name)}
                >
                  {source.name}
                </button>
              ))}
            </div>

            
            {userSources.length > 5 && (
              <button className="carousel-arrow" onClick={() => scrollCarousel("right")}>
                ❯
              </button>
            )}
          </div>
        </div>

        <div className="header-right">
         <button onClick={() => navigate("/edit-sources")} className="nav-btn">Edit Sources</button>
          <button onClick={logout} className="nav-btn logout">Log Out</button>
        </div>
      </header>

      
      <main className="news-page">
        <h1 className="news-title">Latest News</h1>

        {loading && <div className="loading">Loading articles…</div>}
        {error && <div className="error">{error}</div>}

        <div className="articles-grid">
          {articles.map((article, i) => (
            <a
              key={i}
              href={article.url}
              target="_blank"
              rel="noopener noreferrer"
              className="article-card"
            >
              <div className="article-image-container">
                {article.urlToImage ? (
                  <img src={article.urlToImage} alt={article.title} className="article-image" />
                ) : (
                  <div className="article-image placeholder">No Image</div>
                )}
              </div>

              <div className="article-body">
                <span className="article-source">{article.sourceName}</span>
                <h3 className="article-title">{article.title}</h3>
                <p className="article-description">{article.description || article.content}</p>
                <span className="article-date">
                  {new Date(article.publishedAt).toLocaleString("el-GR", {
                    dateStyle: "medium",
                    timeStyle: "short",
                  })}
                </span>
              </div>
            </a>
          ))}
        </div>

        
        {!loading && articles.length > 0 && (
          <div className="pagination-wrapper">
            <div className="pagination">
              <button onClick={handlePrev} disabled={pageNumber === 0}>Previous</button>
              <span>Page {pageNumber + 1} of {totalPages}</span>
              <button onClick={handleNext} disabled={pageNumber + 1 >= totalPages}>Next</button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default NewsPage;
