import { useEffect, useState, useRef } from "react";
import "./SourceSelection.css";

export default function SourceSelection({ user, token }) {
    const [sources, setSources] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selectedSources, setSelectedSources] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const inputRef = useRef(null);

    const fetchSources = async (page, query = "") => {
        setLoading(true);
        try {
            const res = await fetch("http://localhost:8080/mynews/api/sources/search", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    searchCriteria: query ? { name: query } : {},
                    pageNumber: page,
                    pageTotalContentSize: 8
                })
            });
            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
            const data = await res.json();
            setSources(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSources(pageNumber, searchQuery);
    }, [pageNumber, token]);

    useEffect(() => {
        const handler = setTimeout(() => {
            setPageNumber(0);
            fetchSources(0, searchQuery);
        }, 300);
        return () => clearTimeout(handler);
    }, [searchQuery, token]);

    const toggleSource = (id) => {
        setSelectedSources(prev =>
            prev.includes(id)
                ? prev.filter(sid => sid !== id)
                : [...prev, id]
        );
    };

    const handleNext = () => setPageNumber(prev => prev + 1);
    const handlePrev = () => setPageNumber(prev => Math.max(prev - 1, 0));

    const handleSubmit = async () => {
        if (selectedSources.length === 0) return;

        try {
            const res = await fetch("http://localhost:8080/mynews/api/users/sources", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    email: user.email,
                    sourcesIds: selectedSources
                })
            });

            if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);

            // Redirect στη σελίδα των ειδήσεων
            window.location.href = "/news";
        } catch (err) {
            console.error("Failed to save sources:", err);
            alert("Something went wrong while saving your sources.");
        }
    };

    return (
        <div className="source-selection-wrapper">
            <h3 className="centered-text">Select the news sources you want:</h3>

            <div className="search-wrapper">
                <input
                    ref={inputRef}
                    type="text"
                    placeholder="Search a source"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="search-input"
                />
            </div>

            {loading ? (
                <div>Loading sources…</div>
            ) : (
                <>
                    <div className="sources-grid">
                        {sources.map(src => (
                            <div key={src.id} className="source-card">
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={selectedSources.includes(src.id)}
                                        onChange={() => toggleSource(src.id)}
                                    />
                                    <span className="source-info">
                                        <strong>{src.name}</strong>
                                        <em>{src.category}</em>
                                        <p>{src.description}</p>
                                    </span>
                                </label>
                            </div>
                        ))}
                    </div>

                    <div className="pagination-wrapper">
                        <div className="pagination">
                            <button onClick={handlePrev} disabled={pageNumber === 0}>Previous</button>
                            <span>Page {pageNumber + 1} of {totalPages}</span>
                            <button onClick={handleNext} disabled={pageNumber + 1 >= totalPages}>Next</button>
                        </div>

                        {selectedSources.length > 0 && (
                            <button className="submit-button" onClick={handleSubmit}>
                                Submit
                            </button>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}
