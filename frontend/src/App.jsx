import { useEffect, useState } from "react";
import UploadResume from "./components/UploadResume";
import ResumeList from "./components/ResumeList";
import JobDescription from "./components/JobDescription";
import Results from "./components/Results";
import { getResumes } from "./services/api";

function App() {
  const [resumes, setResumes] = useState([]);
  const [results, setResults] = useState(null);

  useEffect(() => {
    loadResumes();
  }, []);

  const loadResumes = async () => {
    try {
      const response = await getResumes();
      setResumes(response.data);
    } catch (error) {
      console.error("Error fetching resumes:", error);
    }
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>🎯 Smart Resume Screener</h1>
        <p>AI-powered resume screening & candidate matching</p>
      </header>

      <section className="card">
        <UploadResume refresh={loadResumes} />
      </section>

      <section className="card">
        <ResumeList resumes={resumes} refresh={loadResumes} />
      </section>

      <section className="card">
        <JobDescription setResults={setResults} />
      </section>

      {results && (
        <section className="card results-card">
          <Results data={results} />
        </section>
      )}
    </div>
  );
}

export default App;
