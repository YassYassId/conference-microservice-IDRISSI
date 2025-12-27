// src/App.js
import React, { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import './App.css';

const API_GATEWAY_BASE = 'http://localhost:8888';

function App() {
  const { keycloak, initialized } = useKeycloak();
  const [conferences, setConferences] = useState([]);
  const [keynotes, setKeynotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [newConference, setNewConference] = useState({
    title: '',
    type: 'ACADEMIC',
    startDate: '',
    duration: 8,
    nbParticipants: 100,
    keynoteIds: []
  });

  const [newKeynote, setNewKeynote] = useState({
    firstName: '',
    lastName: '',
    email: '',
    function: ''
  });

  useEffect(() => {
    if (!initialized || !keycloak.authenticated) return;

    const fetchData = async () => {
      setLoading(true);
      try {
        const headers = {
          'Authorization': `Bearer ${keycloak.token}`,
          'Content-Type': 'application/json',
        };

        const [confRes, keyRes] = await Promise.all([
          fetch(`${API_GATEWAY_BASE}/conference-service/v1/conferences`, { headers }),
          fetch(`${API_GATEWAY_BASE}/keynote-service/v1/keynotes`, { headers })
        ]);

        if (!confRes.ok || !keyRes.ok) throw new Error('Failed to fetch data');

        const confData = await confRes.json();
        const keyData = await keyRes.json();

        setConferences(confData || []);
        setKeynotes(keyData || []);
        setError(null);
      } catch (err) {
        setError('Failed to load data: ' + err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    const interval = setInterval(fetchData, 30000);
    return () => clearInterval(interval);
  }, [initialized, keycloak]);

  const createConference = async (e) => {
    e.preventDefault();
    const headers = { 'Authorization': `Bearer ${keycloak.token}`, 'Content-Type': 'application/json' };
    try {
      const res = await fetch(`${API_GATEWAY_BASE}/conference-service/v1/conferences`, {
        method: 'POST',
        headers,
        body: JSON.stringify(newConference),
      });
      if (res.ok) {
        const created = await res.json();
        setConferences([...conferences, created]);
        setNewConference({ title: '', type: 'ACADEMIC', startDate: '', duration: 8, nbParticipants: 100, keynoteIds: [] });
      }
    } catch (err) {
      alert('Error creating conference');
    }
  };

  const createKeynote = async (e) => {
    e.preventDefault();
    const headers = { 'Authorization': `Bearer ${keycloak.token}`, 'Content-Type': 'application/json' };
    try {
      const res = await fetch(`${API_GATEWAY_BASE}/keynote-service/v1/keynotes`, {
        method: 'POST',
        headers,
        body: JSON.stringify(newKeynote),
      });
      if (res.ok) {
        const created = await res.json();
        setKeynotes([...keynotes, created]);
        setNewKeynote({ firstName: '', lastName: '', email: '', function: '' });
      }
    } catch (err) {
      alert('Error creating keynote');
    }
  };

  if (!initialized) return <div className="loading">Initializing...</div>;
  if (loading) return <div className="loading">Loading conferences & keynotes...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
      <div className="app">
        <header className="header">
          <div className="header-content">
            <h1>Conference Management System</h1>
            <div className="user-info">
              <span>Welcome, {keycloak.tokenParsed?.name || keycloak.tokenParsed?.preferred_username}</span>
              <button onClick={() => keycloak.logout({ redirectUri: window.location.origin })} className="logout-btn">
                Logout
              </button>
            </div>
          </div>
        </header>

        <main className="main">
          <section className="section">
            <h2>📅 Conferences</h2>
            <div className="grid">
              {conferences.map(conf => (
                  <div key={conf.id} className="card conference-card">
                    <h3>{conf.title}</h3>
                    <span className="badge">{conf.type}</span>
                    <div className="info">
                      <p>🕒 Start: {new Date(conf.startDate).toLocaleString()}</p>
                      <p>⏱ Duration: {conf.duration} hours</p>
                      <p>👥 Participants: {conf.nbParticipants}</p>
                      {conf.score && <p>⭐ Average Score: {conf.score.toFixed(1)}</p>}
                    </div>
                    {conf.keynotes?.length > 0 && (
                        <div className="keynotes-list">
                          <strong>🎤 Keynotes:</strong>
                          <ul>
                            {conf.keynotes.map(k => (
                                <li key={k.id}>{k.firstName} {k.lastName} ({k.function})</li>
                            ))}
                          </ul>
                        </div>
                    )}
                  </div>
              ))}
            </div>

            <div className="form-card">
              <h3>Add New Conference</h3>
              <form onSubmit={createConference} className="form">
                <input placeholder="Title" value={newConference.title} onChange={e => setNewConference({...newConference, title: e.target.value})} required />
                <select value={newConference.type} onChange={e => setNewConference({...newConference, type: e.target.value})}>
                  <option>ACADEMIC</option>
                  <option>COMMERCIAL</option>
                </select>
                <input type="datetime-local" value={newConference.startDate} onChange={e => setNewConference({...newConference, startDate: e.target.value})} required />
                <input type="number" placeholder="Duration (hours)" value={newConference.duration} onChange={e => setNewConference({...newConference, duration: Number(e.target.value)})} required />
                <input type="number" placeholder="Expected Participants" value={newConference.nbParticipants} onChange={e => setNewConference({...newConference, nbParticipants: Number(e.target.value)})} required />
                <button type="submit" className="primary-btn">Create Conference</button>
              </form>
            </div>
          </section>

          <section className="section">
            <h2>🎤 Keynote Speakers</h2>
            <div className="grid keynote-grid">
              {keynotes.map(keynote => (
                  <div key={keynote.id} className="card keynote-card">
                    <div className="avatar-placeholder">👤</div>
                    <h3>{keynote.firstName} {keynote.lastName}</h3>
                    <p className="role">{keynote.function}</p>
                    <p className="email">✉️ {keynote.email}</p>
                  </div>
              ))}
            </div>

            <div className="form-card">
              <h3>Add New Keynote Speaker</h3>
              <form onSubmit={createKeynote} className="form">
                <input placeholder="First Name" value={newKeynote.firstName} onChange={e => setNewKeynote({...newKeynote, firstName: e.target.value})} required />
                <input placeholder="Last Name" value={newKeynote.lastName} onChange={e => setNewKeynote({...newKeynote, lastName: e.target.value})} required />
                <input type="email" placeholder="Email" value={newKeynote.email} onChange={e => setNewKeynote({...newKeynote, email: e.target.value})} required />
                <input placeholder="Role / Title" value={newKeynote.function} onChange={e => setNewKeynote({...newKeynote, function: e.target.value})} required />
                <button type="submit" className="primary-btn">Add Speaker</button>
              </form>
            </div>
          </section>
        </main>
      </div>
  );
}

export default App;