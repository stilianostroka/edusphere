import client, { API_BASE_URL } from './client';

export interface CurrentUser { userId: number; profileId: number | null; email: string; role: 'ADMIN'|'TEACHER'|'PARENT'; fullName: string }
export interface Student { id: number; schoolClassId: number|null; className: string|null; firstName: string; lastName: string; status: string; personalId:string; dateOfBirth:string; gender:string|null; address:string|null; enrollmentDate:string }
export interface Teacher { teacherId: number; teacherName: string; teacherSurname: string; teacherEmail: string; gender: string|null }
export interface Parent { id: number; email: string; firstName: string; lastName: string; gender: string|null; address: string|null; phoneNumber: string|null }
export interface SchoolClass { id: number; className: string; classYear: string; maxStudents: number; academicYearLabel: string; supervisorId: number|null; supervisorName: string|null }
export interface Assignment { id: number; schoolClassId: number; schoolClassName: string; subjectId: number; subjectName: string; subjectCode: string; teacherId: number; teacherName: string }
export interface Topic { id:number; teachingAssignmentId:number; schoolClassName:string; subjectName:string; date:string; topicName:string; description:string|null; createdAt:string }
export interface LessonRecord { id:number; topicId:number; topicName:string; subjectName:string; date:string; studentId:number; studentFirstName:string; studentLastName:string; grade:number|null; absent:boolean; justified:boolean; justificationNote:string|null; createdAt:string }
export interface TimetableSlot { id:number; teachingAssignmentId:number; schoolClassName:string; subjectName:string; teacherName:string; dayOfWeek:string; startTime:string; endTime:string }
export interface Notification { id:number; sentByTeacherId:number; sentByTeacherName:string; targetStudentId:number|null; targetStudentName:string|null; targetClassId:number|null; targetClassName:string|null; message:string; sentAt:string }
export interface Modification { id:number; requestedByTeacherId:number; requestedByTeacherName:string; topicId:number|null; lessonRecordId:number|null; semesterExamGradeId:number|null; proposedGrade:number|null; proposedAbsent:boolean; proposedTopicName:string|null; proposedDescription:string|null; proposedExamGrade:number|null; explanation:string; status:string; createdAt:string; reviewedAt:string|null }
export interface FinalGrade { id:number; teachingAssignmentId:number; schoolClassName:string; subjectName:string; studentId:number; studentFirstName:string; studentLastName:string; projectGrade:number|null; cceg:number|null; cfe:number|null; finalGrade:number|null; status:string; submittedAt:string|null; approvedAt:string|null }
export interface GradeRoster { finalGradeId:number|null; studentId:number; studentFirstName:string; studentLastName:string; periods:Array<{sequenceNumber:number; ceg:number|null; examGrade:number|null; examGradeId:number|null}>; cceg:number|null; cfe:number|null; projectGrade:number|null; finalGrade:number|null; status:string }
export interface SubjectClassRoster { teachingAssignmentId:number; subjectName:string; teacherName:string; roster:GradeRoster[] }
export interface ClassAbsenceEntry { id:number; studentId:number; studentFirstName:string; studentLastName:string; subjectName:string; topicName:string; date:string; justified:boolean; justificationNote:string|null; createdAt:string }
export interface Syllabus { id:number; teachingAssignmentId:number; schoolClassName:string; subjectName:string; fileUrl:string; originalFileName:string; contentType:string; uploadedAt:string }
export interface Meeting { id:number; classId:number; teacherId:number; meetingTime:string; Topic:string }
export interface ForumPost { id:number; authorId:number; authorName:string; content:string; createdAt:string }
export interface Diploma { id:number; studentId:number; studentName:string; academicYearId:number; academicYearLabel:string; fileUrl:string; generatedAt:string }
export interface SemesterExamGrade { id:number; teachingAssignmentId:number; schoolClassName:string; subjectName:string; studentId:number; studentFirstName:string; studentLastName:string; gradingPeriodId:number; gradingPeriodSequenceNumber:number; examGrade:number; createdAt:string }

export const fileUrl = (path: string) => path.startsWith('http') ? path : `${API_BASE_URL.replace(/\/api$/, '')}${path}`;
export const getMe = () => client.get<CurrentUser>('/me').then(r => r.data);

export const studentsApi = {
  all: (academicYearId?:number|null) => client.get<Student[]>('/students', {params: academicYearId ? {academicYearId} : {}}).then(r => r.data),
  one: (id:number) => client.get<Student>(`/students/${id}`).then(r => r.data),
  byClass: (classId:number) => client.get<Student[]>(`/students/class/${classId}`).then(r => r.data),
  create: (data:Record<string, unknown>) => client.post<Student>('/students', data).then(r => r.data),
  update: (id:number, data:Record<string, unknown>) => client.put<Student>(`/students/${id}`, data).then(r => r.data),
  enroll: (id:number, classId:number) => client.post<Student>(`/students/${id}/enroll`, {classId}).then(r => r.data),
  status: (id:number, status:string) => client.put(`/students/${id}/status`, {status}),
  absences: (id:number) => client.get<LessonRecord[]>(`/lesson-records/student/${id}/absences`).then(r => r.data),
  grades: (id:number) => client.get<LessonRecord[]>(`/lesson-records/student/${id}/grades`).then(r => r.data)
};
export const teachersApi = {
  all: () => client.get<Teacher[]>('/teachers').then(r => r.data),
  create: (data:Record<string, unknown>) => client.post<Teacher>('/teachers', data).then(r => r.data),
  update: (id:number, data:Record<string, unknown>) => client.put<Teacher>(`/teachers/${id}`, data).then(r => r.data)
};
export const parentsApi = {
  all: () => client.get<Parent[]>('/parents').then(r => r.data),
  create: (data:Record<string, unknown>) => client.post<Parent>('/parents', data).then(r => r.data),
  update: (id:number, data:Record<string, unknown>) => client.put<Parent>(`/parents/${id}`, data).then(r => r.data),
  children: (id:number) => client.get<Student[]>(`/parents/${id}/students`).then(r => r.data),
  myChildren: () => client.get<Student[]>('/parents/me/students').then(r => r.data),
  link: (parentId:number, studentId:number) => client.post(`/parents/${parentId}/students`, {studentId}),
  unlink: (parentId:number, studentId:number) => client.delete(`/parents/${parentId}/students/${studentId}`)
};
export const classesApi = {
  all: (academicYearId?:number|null) => client.get<SchoolClass[]>('/classes', {params: academicYearId ? {academicYearId} : {}}).then(r=>r.data),
  supervised: (academicYearId?:number|null) => client.get<SchoolClass>('/classes/supervised/me', {params: academicYearId ? {academicYearId} : {}}).then(r=>r.data)
};
export const assignmentsApi = {
  all: (academicYearId?:number|null) => client.get<Assignment[]>('/teaching-assignments', {params: academicYearId ? {academicYearId} : {}}).then(r=>r.data),
  mine: (academicYearId?:number|null) => client.get<Assignment[]>('/teaching-assignments/me', {params: academicYearId ? {academicYearId} : {}}).then(r=>r.data)
};
export const timetableApi = {
  teacher: (teacherId:number, academicYearId?:number|null) => client.get<TimetableSlot[]>(`/timetable/teacher/${teacherId}`, {params: academicYearId ? {academicYearId} : {}}).then(r=>r.data),
  assignment: (assignmentId:number) => client.get<TimetableSlot[]>(`/timetable/teaching-assignment/${assignmentId}`).then(r=>r.data),
  create: (data:Record<string,unknown>) => client.post<TimetableSlot>('/timetable', data).then(r=>r.data),
  update: (id:number,data:Record<string,unknown>) => client.put<TimetableSlot>(`/timetable/${id}`,data).then(r=>r.data)
};
export const topicsApi = {
  all: (assignmentId:number) => client.get<Topic[]>(`/topics/teaching-assignment/${assignmentId}`).then(r=>r.data),
  create: (data:Record<string,unknown>) => client.post<Topic>('/topics',data).then(r=>r.data),
  update: (id:number,data:Record<string,unknown>) => client.put<Topic>(`/topics/${id}`,data).then(r=>r.data),
  remove: (id:number) => client.delete(`/topics/${id}`),
  records: (topicId:number) => client.get<LessonRecord[]>(`/lesson-records/topic/${topicId}`).then(r=>r.data),
  grade: (topicId:number,studentId:number,grade:number) => client.post<LessonRecord>('/lesson-records/grade',{topicId,studentId,grade}).then(r=>r.data),
  absent: (topicId:number,studentId:number) => client.post<LessonRecord>('/lesson-records/absent',{topicId,studentId}).then(r=>r.data),
  updateGrade: (id:number,grade:number) => client.put<LessonRecord>(`/lesson-records/${id}/grade`,{grade}).then(r=>r.data),
  markAbsent: (id:number) => client.put<LessonRecord>(`/lesson-records/${id}/mark-absent`).then(r=>r.data),
  justify: (id:number,note:string) => client.put<LessonRecord>(`/lesson-records/${id}/justify`,{note}).then(r=>r.data),
  removeRecord: (id:number) => client.delete(`/lesson-records/${id}`)
};
export const lessonRecordsApi = {
  classAbsences: (classId:number) => client.get<ClassAbsenceEntry[]>(`/lesson-records/class/${classId}/absences`).then(r=>r.data)
};
export const gradesApi = {
  roster: (assignmentId:number) => client.get<GradeRoster[]>(`/final-subject-grades/roster/${assignmentId}`).then(r=>r.data),
  classRoster: (classId:number) => client.get<SubjectClassRoster[]>(`/final-subject-grades/class/${classId}`).then(r=>r.data),
  submitted: () => client.get<FinalGrade[]>('/final-subject-grades/submitted').then(r=>r.data),
  approve: (id:number) => client.put<FinalGrade>(`/final-subject-grades/${id}/approve`).then(r=>r.data),
  reject: (id:number) => client.put<FinalGrade>(`/final-subject-grades/${id}/reject`).then(r=>r.data),
  project: (teachingAssignmentId:number,studentId:number,projectGrade:number) => client.post('/final-subject-grades/project-grade',{teachingAssignmentId,studentId,projectGrade}),
  submit: (id:number) => client.post(`/final-subject-grades/${id}/submit`),
  exam: (teachingAssignmentId:number,studentId:number,gradingPeriodId:number,examGrade:number) => client.post('/semester-exam-grades',{teachingAssignmentId,studentId,gradingPeriodId,examGrade}),
  examsFor: (studentId:number,teachingAssignmentId:number) => client.get<SemesterExamGrade[]>(`/semester-exam-grades/student/${studentId}/teaching-assignment/${teachingAssignmentId}`).then(r=>r.data),
  removeProject: (finalGradeId:number) => client.delete(`/final-subject-grades/${finalGradeId}/project-grade`),
  withdraw: (finalGradeId:number) => client.post(`/final-subject-grades/${finalGradeId}/withdraw`),
  removeExam: (examGradeId:number) => client.delete(`/semester-exam-grades/${examGradeId}`)
};
export const modificationsApi = {
  pending: () => client.get<Modification[]>('/modification-requests/pending').then(r=>r.data),
  mine: (teacherId:number) => client.get<Modification[]>(`/modification-requests/teacher/${teacherId}`).then(r=>r.data),
  topic: (data:Record<string,unknown>) => client.post('/modification-requests/topic',data),
  grade: (data:Record<string,unknown>) => client.post('/modification-requests/grade',data),
  absence: (data:Record<string,unknown>) => client.post('/modification-requests/absence',data),
  exam: (data:Record<string,unknown>) => client.post('/modification-requests/exam-grade',data)
};
export const notificationsApi = {
  sent: () => client.get<Notification[]>('/notifications/sent').then(r=>r.data),
  inbox: () => client.get<Notification[]>('/notifications/inbox').then(r=>r.data),
  student: (studentId:number,message:string) => client.post('/notifications/student',{studentId,message}),
  schoolClass: (classId:number,message:string) => client.post('/notifications/class',{classId,message})
};
export const meetingsApi = {
  byClass: (classId:number) => client.get<Meeting[]>(`/parent-meetings/class/${classId}`).then(r=>r.data),
  create: (classId:number,meetingTime:string,topic:string) => client.post('/parent-meetings',{classId,meetingTime,topic}),
  update: (id:number,classId:number,meetingTime:string,topic:string) => client.put(`/parent-meetings/${id}`,{classId,meetingTime,topic}),
  remove: (id:number) => client.delete(`/parent-meetings/${id}`)
};
export const syllabusApi = {
  mine: (teacherId:number) => client.get<Syllabus[]>(`/syllabi/teacher/${teacherId}`).then(r=>r.data),
  upload: async (teachingAssignmentId:number,file:File) => { const data=new FormData(); data.append('file',file); data.append('teachingAssignmentId',String(teachingAssignmentId)); return client.post<Syllabus>('/syllabi/upload',data,{headers:{'Content-Type':'multipart/form-data'}}).then(r=>r.data); }
};
export const forumApi = {
  all: () => client.get<ForumPost[]>('/forum').then(r=>r.data),
  create: (content:string) => client.post<ForumPost>('/forum',{content}).then(r=>r.data),
  remove: (id:number) => client.delete(`/forum/${id}`)
};
export const diplomasApi = {
  all: () => client.get<Diploma[]>('/diplomas').then(r=>r.data),
  student: (studentId:number) => client.get<Diploma[]>(`/diplomas/student/${studentId}`).then(r=>r.data),
  generate: (studentId:number,academicYearId:number) => client.post<Diploma>('/diplomas/generate',{studentId,academicYearId}).then(r=>r.data)
};
