import {useEffect,useState} from 'react';
import {teachersApi,type Teacher} from '../api/system';
import {Icon} from '../components/Icon';
import {useMessage} from '../context/MessageContext';
type Form={name:string;surname:string;email:string;password:string;gender:string};
const empty:Form={name:'',surname:'',email:'',password:'',gender:'MALE'};
export function TeachersPage(){
 const {showError}=useMessage();
 const [rows,setRows]=useState<Teacher[]>([]),[form,setForm]=useState<Form>(empty),[editing,setEditing]=useState<Teacher|null>(null),[open,setOpen]=useState(false);
 const load=async()=>{try{const t=await teachersApi.all();setRows(t.slice().sort((a,b)=>`${a.teacherName} ${a.teacherSurname}`.localeCompare(`${b.teacherName} ${b.teacherSurname}`)));}catch{showError('Could not load teachers.');}};useEffect(()=>{void load();},[]);
 const edit=(t:Teacher)=>{setEditing(t);setForm({name:t.teacherName,surname:t.teacherSurname,email:t.teacherEmail,password:'',gender:t.gender??'MALE'});setOpen(true);};
 const save=async()=>{try{editing?await teachersApi.update(editing.teacherId,form):await teachersApi.create(form);setOpen(false);setForm(empty);setEditing(null);await load();}catch{showError('Teacher could not be saved. Check email uniqueness and required fields.');}};
 return <div><div className="breadcrumb">Home › <span className="current">Teachers</span></div><div className="panel"><div className="panel-header"><div className="panel-icon"><Icon name="teacher" /></div><div><div className="panel-eyebrow">Staff</div><h3>Teacher management</h3></div></div><div className="filter-bar"><div style={{flex:1}}>{rows.length} teachers</div><button className="btn-primary" onClick={()=>{setEditing(null);setForm(empty);setOpen(true);}}>Add teacher</button></div><table className="data-table"><thead><tr><th>#</th><th>Name</th><th>Email</th><th>Gender</th><th></th></tr></thead><tbody>{rows.map((t,i)=><tr key={t.teacherId}><td>{i+1}</td><td><strong>{t.teacherName} {t.teacherSurname}</strong></td><td>{t.teacherEmail}</td><td>{t.gender??'—'}</td><td><button className="btn-text" onClick={()=>edit(t)}>Edit</button></td></tr>)}</tbody></table></div>
 {open&&<div className="modal-backdrop"><div className="modal-card"><h3>{editing?'Edit teacher':'Add teacher'}</h3>{(['name','surname','email','password'] as const).map(k=><div className="field" key={k}><label>{k[0].toUpperCase()+k.slice(1)}{k==='password'&&editing?' (leave blank to keep current)':''}</label><input type={k==='password'?'password':'text'} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/></div>)}<div className="field"><label>Gender</label><select value={form.gender} onChange={e=>setForm({...form,gender:e.target.value})}><option>MALE</option><option>FEMALE</option><option>OTHER</option></select></div><div className="modal-actions"><button className="btn-outline" onClick={()=>setOpen(false)}>Cancel</button><button className="btn-primary" onClick={()=>void save()}>Save</button></div></div></div>}
 </div>;
}
