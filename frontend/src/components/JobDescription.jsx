import { useState } from "react";
import { matchResume } from "../services/api";

function jobDescription({ setResults }) {
  const [jobDescription, setJobDescription] = useState("");
  const [threshold, setThreshold] = useState(6);

  const runMatch = async () => {
    try {
      const response = await matchResume(jobDescription, threshold);

      if (response.data.success) {
        setResults(response.data);
      } else {
        alert(response.data.error);
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        alert(error.response.data.error);
      } else {
        alert("An error occurred");
      }
    }
  };

  return (
    <div>
      <h2>💼 Job Description</h2>

      <textarea
        className="job-textarea"
        value={jobDescription}
        onChange={(e) => setJobDescription(e.target.value)}
        rows={10}
      />

      <div className="threshold-row">
        <input
          className="threshold-input"
          type="number"
          value={threshold}
          onChange={(e) => setThreshold(e.target.value)}
          min={1}
          max={10}
        />

        <span>Match Threshold</span>
      </div>

      <button className="btn primary" onClick={runMatch}>
        🔍 Screen Candidates
      </button>
    </div>
  );
}

export default jobDescription;
