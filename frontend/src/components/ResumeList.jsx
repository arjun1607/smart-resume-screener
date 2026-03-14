import { deleteResume, deleteAllResumes } from "../services/api";

function ResumeList({ resumes, refresh }) {
  const remove = async (id) => {
    await deleteResume(id);
    refresh();
  };

  const clearAll = async () => {
    await deleteAllResumes();
    refresh();
  };

  return (
    <div>
      <h2>📄 Uploaded Resumes ({resumes.length})</h2>

      <button
        className="btn danger"
        onClick={clearAll}
        disabled={resumes.length === 0}
      >
        Clear All
      </button>

      <div className="resume-list">
        {resumes.map((resume) => (
          <div key={resume.id} className="resume-item">
            <div>
              <strong>{resume.candidateName}</strong>
              <p>
                {resume.email} | {resume.phone}
              </p>
            </div>

            <button className="btn small" onClick={() => remove(resume.id)}>
              Delete
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ResumeList;
