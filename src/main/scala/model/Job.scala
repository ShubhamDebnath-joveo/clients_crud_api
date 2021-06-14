package model

case class Job(jobId: String, category: String, title: String, minExperience: Int, tags: List[String], isValid: Boolean)
