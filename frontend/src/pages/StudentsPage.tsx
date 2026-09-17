import { useEffect, useState } from 'react';
import { classesApi, studentsApi, type SchoolClass, type Student } from '../api/system';
import { useAcademicYear } from '../context/AcademicYearContext';
import { Icon } from '../components/Icon';
import { useMessage } from '../context/MessageContext';

type Form = { firstName:string; lastName:string; dateOfBirth:string; personalId:string; gender:string; address:string; classId:string };
const empty: Form = {firstName:'',lastName:'',dateOfBirth:'',personalId:'',gender:'MALE',address:'',classId:''};

export function StudentsPage() {
  const {selectedYearId,selectedYear}=useAcademicYear();
  const {showError}=useMessage();
  const [rows,setRows]=useState<Student[]>([]), [classes,setClasses]=useState<SchoolClass[]>([]);
  const [form,setForm]=useState<Form>(empty), [editing,setEditing]=useState<Student|null>(null);
  const [open,setOpen]=useState(false), [search,setSearch]=useState('');
  const load=async()=>{try{const [s,c]=await Promise.all([studentsApi.all(selectedYearId),classesApi.all(selectedYearId)]);setRows(s);setClasses(c);}catch{showError('Could not load students. Check that the backend is running.');}};
  useEffect(()=>{void load();},[selectedYearId]);

  const startEdit=(s:Student)=>{setEditing(s);setForm({firstName:s.firstName,lastName:s.lastName,dateOfBirth:s.dateOfBirth,personalId:s.personalId,gender:s.gender??'MALE',address:s.address??'',classId:s.schoolClassId?.toString()??''});setOpen(true);};
  const save=async()=>{try{
    if(editing){await studentsApi.update(editing.id,{firstName:form.firstName,lastName:form.lastName,dateOfBirth:form.dateOfBirth,personalId:form.personalId,gender:form.gender,address:form.address});if(form.classId&&Number(form.classId)!==editing.schoolClassId)await studentsApi.enroll(editing.id,Number(form.classId));}
    else {const created=await studentsApi.create({firstName:form.firstName,lastName:form.lastName,dateOfBirth:form.dateOfBirth,personalId:form.personalId,gender:form.gender,address:form.address});if(form.classId)await studentsApi.enroll(created.id,Number(form.classId));}
    setOpen(false);setEditing(null);setForm(empty);await load();
  }catch{showError('Student could not be saved. Check required fields and duplicate personal ID.');}};
  const filtered=rows.filter(s=>`${s.firstName} ${s.lastName} ${s.className??''}`.toLowerCase().includes(search.toLowerCase())).sort((a,b)=>`${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`));
  return <div><div className="breadcrumb">Home › <span className="current">Students</span></div>
    <div className="panel"><div className="panel-header"><div className="panel-icon"><Icon name="graduation-cap" /></div><div><div className="panel-eyebrow">{selectedYear?.label??'All years'}</div><h3>Student management</h3></div></div>
      <div className="filter-bar"><div className="field" style={{marginBottom:0}}><label>Search</label><input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Name or class"/></div><button className="btn-primary" onClick={()=>{setEditing(null);setForm(empty);setOpen(true);}}>Add student</button></div>
      <table className="data-table"><thead><tr><th>#</th><th>Student</th><th>Class</th><th>Status</th><th>Actions</th></tr></thead><tbody>{filtered.map((s,i)=><tr key={s.id}><td>{i+1}</td><td><strong>{s.firstName} {s.lastName}</strong></td><td>{s.className??'Not enrolled'}</td><td><span className={`status-pill ${s.status==='ACTIVE'?'active':'pending'}`}>{s.status}</span></td><td><button className="btn-text" onClick={()=>startEdit(s)}>Edit / enroll</button></td></tr>)}</tbody></table>
      {!filtered.length&&<div className="empty-state">No students found.</div>}
    </div>
    {open&&<div className="modal-backdrop"><div className="modal-card wide"><h3>{editing?'Edit student':'Add student'}</h3><div className="form-grid">
      {(['firstName','lastName','dateOfBirth','personalId','address'] as const).map(k=><div className="field" key={k}><label>{k.replace(/([A-Z])/g,' $1')}</label><input type={k==='dateOfBirth'?'date':'text'} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/></div>)}
      <div className="field"><label>Gender</label><select value={form.gender} onChange={e=>setForm({...form,gender:e.target.value})}><option>MALE</option><option>FEMALE</option><option>OTHER</option></select></div>
      <div className="field"><label>Class</label><select value={form.classId} onChange={e=>setForm({...form,classId:e.target.value})}><option value="">Not enrolled</option>{classes.map(c=><option value={c.id} key={c.id}>{c.className}</option>)}</select></div>
    </div><p className="hint">When editing, enter the personal ID and date of birth again because those fields are validated by the backend.</p><div className="modal-actions"><button className="btn-outline" onClick={()=>setOpen(false)}>Cancel</button><button className="btn-primary" onClick={()=>void save()}>Save student</button></div></div></div>}
  </div>;
}
