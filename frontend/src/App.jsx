import React, { useState } from 'react'
import './App.css'

function App() {
    const [count, setCount] = useState(0)

    return (
        <div className="App">
            <header className="App-header">
                <h1>Easy Base Application</h1>
                <p>Welcome to your Spring + React application!</p>
                <div className="card">
                    <button onClick={() => setCount((count) => count + 1)}>
                        count is {count}
                    </button>
                </div>
                <div className="status">
                    <h3>Application Status</h3>
                    <p>✅ Frontend: Running</p>
                    <p>✅ Backend: Spring Framework</p>
                    <p>✅ Database: HSQLDB (Development)</p>
                </div>
            </header>
        </div>
    )
}

export default App
