import { useEffect, useState, useRef } from "react";
import "./SourceSelection.css";
import { useAuth } from "../AuthFlow";
import { useNavigate } from "react-router-dom";

export default function SourceSelection({ isFirstTime = false }) {
    const [sources, setSources] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selectedSources, setSelectedSources] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [selectedObjects, setSelectedObjects] = useState([]);
    const [isPanelOpen, setIsPanelOpen] = useState(false);
    const { token } = useAuth();
    const navigate = useNavigate();
    
    const fetchUserSources = async () => {
        try {
            const res = await fetch("http://localhost:8080/mynews/api/users/sources", {
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (res.ok) {
                const data = await res.json();
                setSelectedSources(data.map(s => s.id));
                setSelectedObjects(data);
            }
        } catch (err) {
            console.error("Error fetching user sources:", err);
        }
    };

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
        fetchUserSources(); 
    }, [token]);

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

    useEffect(() => {
        
        const newMatches = sources.filter(src => selectedSources.includes(src.id));
        
        setSelectedObjects(prev => {
            
            const combined = [...prev, ...newMatches];
            
            const unique = Array.from(new Map(combined.map(item => [item.id, item])).values());
            return unique.filter(u => selectedSources.includes(u.id));
        });
    }, [selectedSources, sources]);


    const toggleSource = (id) => {
        setSelectedSources(prev =>
            prev.includes(id) ? prev.filter(sid => sid !== id) : [...prev, id]
        );
    };

    const handleSubmit = async () => {
        try {
            const res = await fetch("http://localhost:8080/mynews/api/users/sources", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({ sourcesIds: selectedSources })
            });

            if (!res.ok) throw new Error("Failed to save");
            
            navigate("/");
        } catch (err) {
            alert("Σφάλμα κατά την αποθήκευση.");
        }
    };

    

    return (
        <div className="source-selection-container">
           <aside className={`selected-summary-panel ${isPanelOpen ? 'visible' : ''}`}>
            <div className="summary-header">
                <h4>Selected Sources ({selectedSources.length})</h4>
                {/* Κουμπί για κλείσιμο μέσα στο πάνελ */}
                <button className="close-panel-btn" onClick={() => setIsPanelOpen(false)}>×</button>
            </div>
            <div className="summary-list">
                {selectedObjects.map(obj => (
                    <div key={obj.id} className="summary-tag">
                        <span>{obj.name}</span>
                        <button onClick={() => toggleSource(obj.id)}>×</button>
                    </div>
                ))}
            </div>
        </aside>
        {isPanelOpen && <div className="panel-overlay" onClick={() => setIsPanelOpen(false)}></div>}
            <header className="selection-header">
                <h2>{isFirstTime ? "Welcome! Select your news sources" : "Edit your news sources"}</h2>
                <div className="search-row">
                <div className="search-box">
                    <input
                        type="text"
                        placeholder="Search for a source..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>
                <button 
                    className="show-selected-btn"
                    onClick={() => setIsPanelOpen(true)}
                >
                    Show selected sources ({selectedSources.length})
                </button>
            </div>
            </header>

            <main className="selection-content">
                {loading ? (
                    <div className="loader">Loading sources...</div>
                ) : (
                    <div className="sources-grid">
                        {sources.map(src => (
                            <div 
                                key={src.id} 
                                className={`source-card ${selectedSources.includes(src.id) ? 'selected' : ''}`}
                                onClick={() => toggleSource(src.id)}
                            >
                                <div className="card-header">
                                    <input 
                                        type="checkbox" 
                                        readOnly 
                                        checked={selectedSources.includes(src.id)} 
                                    />
                                    <strong>{src.name}</strong>
                                </div>
                                <span className="category-tag">{src.category}</span>
                                <p className="description-text">{src.description}</p>
                            </div>
                        ))}
                    </div>
                )}
            </main>

            <footer className="selection-footer">
                <div className="pagination">
                    <button onClick={() => setPageNumber(p => Math.max(0, p - 1))} disabled={pageNumber === 0}>Prev</button>
                    <span>{pageNumber + 1} / {totalPages}</span>
                    <button onClick={() => setPageNumber(p => p + 1)} disabled={pageNumber + 1 >= totalPages}>Next</button>
                </div>
                
                <button 
                    className="submit-btn" 
                    disabled={selectedSources.length === 0}
                    onClick={handleSubmit}
                >
                    Save Changes
                </button>
            </footer>
        </div>
    );
}