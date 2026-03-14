import { uploadResume } from "../services/api";

function UploadResume({ refresh }) {
  const handleFileChange = async (e) => {
    const files = e.target.files;

    for (let i = 0; i < files.length; i++) {
      try {
        const res = await uploadResume(files[i]);

        if (res.data.success) {
          alert("Uploaded: " + res.data.candidateName);
        } else {
          alert(res.data.error);
        }
      } catch (error) {
        if (error.response && error.response.status === 400) {
          alert(error.response.data.error);
        } else {
          alert("An error occurred");
        }
      }
    }

    refresh();
  };

  return (
    <div className="upload-section">
      <h2>📤 Upload Resume</h2>

      <input
        className="file-input"
        type="file"
        accept=".pdf,.doc,.docx"
        multiple
        onChange={handleFileChange}
      />
    </div>
  );
}

export default UploadResume;
