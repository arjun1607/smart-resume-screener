import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const uploadResume = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  return API.post("/upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const matchResume = (jobDescription, threshold) => {
  return API.post("/match", {
    jobDescription,
    threshold,
  });
};

export const getResumes = () => API.get("/resumes");

export const deleteResume = (id) => API.delete(`/resumes/${id}`);

export const deleteAllResumes = () => API.delete("/resumes");
