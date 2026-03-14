function results({ data }) {
  return (
    <div>
      <h2>✅ Screening Results</h2>

      <div className="stats">
        <div>
          <strong>{data.totalCandidates}</strong>
          <span>Total Candidates</span>
        </div>

        <div>
          <strong>{data.shortlistedCount}</strong>
          <span>Shortlisted</span>
        </div>
      </div>

      {data.results.map((candidate) => (
        <div key={candidate.resumeId} className="result-card">
          <div className="result-header">
            <h3>{candidate.candidateName}</h3>
            <div className="score-badge">{candidate.matchScore}/10</div>
          </div>

          <p>
            <strong>📞 Phone:</strong> {candidate.phone}
          </p>
          <p>
            <strong>📧 Email:</strong> {candidate.email}
          </p>
          <p>
            <strong>💻 Skills:</strong> {candidate.skills}
          </p>

          <div className="justification">
            <h4>AI Analysis</h4>

            <p style={{ whiteSpace: "pre-line" }}>{candidate.justification}</p>
          </div>
        </div>
      ))}
    </div>
  );
}

export default results;
