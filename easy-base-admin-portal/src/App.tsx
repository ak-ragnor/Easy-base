import React, { useState, useEffect, createContext, useContext } from 'react';

// Styles
const styles = {
    container: {
        minHeight: '100vh',
        backgroundColor: '#f5f5f5',
        fontFamily: 'Arial, sans-serif',
    },
    header: {
        backgroundColor: '#2196f3',
        color: 'white',
        padding: '1rem 2rem',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    },
    headerTitle: {
        margin: 0,
        fontSize: '1.5rem',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    main: {
        padding: '2rem',
        maxWidth: '1200px',
        margin: '0 auto',
    },
    card: {
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '1.5rem',
        marginBottom: '1.5rem',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: '1.5rem',
        marginBottom: '2rem',
    },
    loginContainer: {
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    },
    loginBox: {
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '12px',
        boxShadow: '0 10px 40px rgba(0,0,0,0.15)',
        width: '100%',
        maxWidth: '400px',
    },
    formGroup: {
        marginBottom: '1rem',
    },
    label: {
        display: 'block',
        marginBottom: '0.5rem',
        fontWeight: '600',
        color: '#333',
    },
    input: {
        width: '100%',
        padding: '0.75rem',
        border: '1px solid #ddd',
        borderRadius: '4px',
        fontSize: '1rem',
        boxSizing: 'border-box',
    },
    button: {
        width: '100%',
        padding: '0.75rem',
        backgroundColor: '#2196f3',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        fontSize: '1rem',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'background-color 0.3s',
    },
    buttonHover: {
        backgroundColor: '#1976d2',
    },
    alert: {
        padding: '0.75rem',
        borderRadius: '4px',
        marginBottom: '1rem',
    },
    alertError: {
        backgroundColor: '#ffebee',
        color: '#c62828',
        border: '1px solid #ef5350',
    },
    alertSuccess: {
        backgroundColor: '#e8f5e9',
        color: '#2e7d32',
        border: '1px solid #66bb6a',
    },
    chip: {
        display: 'inline-block',
        padding: '0.25rem 0.75rem',
        borderRadius: '12px',
        fontSize: '0.875rem',
        fontWeight: '600',
        marginRight: '0.5rem',
    },
    chipSuccess: {
        backgroundColor: '#e8f5e9',
        color: '#2e7d32',
    },
    chipPrimary: {
        backgroundColor: '#e3f2fd',
        color: '#1976d2',
    },
    chipOutlined: {
        backgroundColor: 'transparent',
        border: '1px solid #ddd',
        color: '#666',
    },
    sidebar: {
        position: 'fixed',
        left: 0,
        top: 0,
        width: '250px',
        height: '100vh',
        backgroundColor: 'white',
        boxShadow: '2px 0 4px rgba(0,0,0,0.1)',
        transform: 'translateX(-100%)',
        transition: 'transform 0.3s',
        zIndex: 1000,
    },
    sidebarOpen: {
        transform: 'translateX(0)',
    },
    menuItem: {
        padding: '1rem 1.5rem',
        borderBottom: '1px solid #eee',
        cursor: 'pointer',
        transition: 'background-color 0.3s',
    },
    menuItemHover: {
        backgroundColor: '#f5f5f5',
    },
    overlay: {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.5)',
        display: 'none',
        zIndex: 999,
    },
    overlayVisible: {
        display: 'block',
    },
};

// API Service
const API_BASE_URL = 'http://localhost:8080/easy-base/api';

class ApiService {
    static async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const config = {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers,
            },
        };

        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, config);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Request failed');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    static get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    static post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }
}

// Auth Context
const AuthContext = createContext(null);

const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};

// Login Component
const LoginPage = () => {
    const { login } = useAuth();
    const [email, setEmail] = useState('admin@easybase.com');
    const [password, setPassword] = useState('admin123');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await login(email, password);
        } catch (err) {
            setError('Invalid credentials');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.loginContainer}>
            <div style={styles.loginBox}>
                <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
                    <h1 style={{ color: '#2196f3', margin: 0 }}>EasyBase</h1>
                    <p style={{ color: '#666', marginTop: '0.5rem' }}>Admin Portal</p>
                </div>

                {error && (
                    <div style={{ ...styles.alert, ...styles.alertError }}>
                        {error}
                    </div>
                )}

                <div style={styles.formGroup}>
                    <label style={styles.label}>Email</label>
                    <input
                        type="email"
                        style={styles.input}
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter your email"
                        required
                    />
                </div>

                <div style={styles.formGroup}>
                    <label style={styles.label}>Password</label>
                    <input
                        type="password"
                        style={styles.input}
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        required
                    />
                </div>

                <button
                    style={styles.button}
                    onClick={handleSubmit}
                    disabled={loading}
                >
                    {loading ? 'Signing in...' : 'Sign In'}
                </button>

                <p style={{ textAlign: 'center', marginTop: '1rem', color: '#666', fontSize: '0.875rem' }}>
                    Phase 1: Foundation & Project Setup
                </p>
            </div>
        </div>
    );
};

// Dashboard Component
const Dashboard = () => {
    const [systemInfo, setSystemInfo] = useState(null);
    const [health, setHealth] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchSystemData();
    }, []);

    const fetchSystemData = async () => {
        try {
            setLoading(true);
            const [healthData, infoData] = await Promise.all([
                ApiService.get('/system/health'),
                ApiService.get('/system/info')
            ]);

            setHealth(healthData.data);
            setSystemInfo(infoData.data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '2rem' }}>
                <p>Loading system data...</p>
            </div>
        );
    }

    return (
        <div>
            <h2>Dashboard</h2>

            {error && (
                <div style={{ ...styles.alert, ...styles.alertError }}>
                    Error: {error}
                </div>
            )}

            <div style={styles.grid}>
                <div style={styles.card}>
                    <h3 style={{ marginTop: 0, color: '#2e7d32' }}>✓ System Health</h3>
                    {health && (
                        <div>
                            <p><strong>Status:</strong> <span style={{ ...styles.chip, ...styles.chipSuccess }}>{health.status}</span></p>
                            <p><strong>Version:</strong> {health.version}</p>
                            <p><strong>Service:</strong> {health.service || 'EasyBase Platform'}</p>
                            <p><strong>Last Check:</strong> {new Date().toLocaleTimeString()}</p>
                        </div>
                    )}
                </div>

                <div style={styles.card}>
                    <h3 style={{ marginTop: 0, color: '#1976d2' }}>💾 System Information</h3>
                    {systemInfo && (
                        <div>
                            <p><strong>App Version:</strong> {systemInfo.appVersion}</p>
                            <p><strong>DB Version:</strong> {systemInfo.dbVersion}</p>
                            <p><strong>Status:</strong> <span style={{ ...styles.chip, ...styles.chipPrimary }}>{systemInfo.status}</span></p>
                            <p><strong>System ID:</strong> {systemInfo.id || 'N/A'}</p>
                        </div>
                    )}
                </div>
            </div>

            <div style={styles.card}>
                <h3>Development Phases</h3>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                    <span style={{ ...styles.chip, ...styles.chipSuccess }}>✓ Phase 1: Foundation</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 2: Core Tables</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 3: User System</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 4: Dynamic APIs</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 5: File System</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 6: Actions</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 7: Plugin System</span>
                    <span style={{ ...styles.chip, ...styles.chipOutlined }}>Phase 8: Production</span>
                </div>
            </div>

            <div style={{ textAlign: 'center', marginTop: '2rem' }}>
                <button
                    style={{ ...styles.button, width: 'auto', padding: '0.75rem 2rem' }}
                    onClick={fetchSystemData}
                >
                    Refresh System Status
                </button>
            </div>
        </div>
    );
};

// Sidebar Component
const Sidebar = ({ isOpen, onClose }) => {
    const menuItems = [
        { text: 'Dashboard', icon: '📊' },
        { text: 'Tables', icon: '📋' },
        { text: 'Users', icon: '👥' },
        { text: 'File System', icon: '📁' },
        { text: 'Plugins', icon: '🔌' },
        { text: 'Settings', icon: '⚙️' },
    ];

    return (
        <>
            <div
                style={{
                    ...styles.overlay,
                    ...(isOpen ? styles.overlayVisible : {})
                }}
                onClick={onClose}
            />
            <div
                style={{
                    ...styles.sidebar,
                    ...(isOpen ? styles.sidebarOpen : {})
                }}
            >
                <div style={{ padding: '1.5rem', borderBottom: '1px solid #eee' }}>
                    <h3 style={{ margin: 0 }}>Navigation</h3>
                </div>
                {menuItems.map((item) => (
                    <div
                        key={item.text}
                        style={styles.menuItem}
                        onClick={onClose}
                    >
                        <span style={{ marginRight: '0.75rem' }}>{item.icon}</span>
                        {item.text}
                    </div>
                ))}
            </div>
        </>
    );
};

// Main Layout Component
const MainLayout = ({ children }) => {
    const { user, logout } = useAuth();
    const [sidebarOpen, setSidebarOpen] = useState(false);

    return (
        <div style={styles.container}>
            <header style={styles.header}>
                <h1 style={styles.headerTitle}>
          <span>
            <button
                onClick={() => setSidebarOpen(!sidebarOpen)}
                style={{
                    background: 'none',
                    border: 'none',
                    color: 'white',
                    fontSize: '1.5rem',
                    cursor: 'pointer',
                    marginRight: '1rem',
                }}
            >
              ☰
            </button>
            💾 EasyBase Admin Portal
          </span>
                    <span style={{ fontSize: '0.875rem' }}>
            {user?.email} |{' '}
                        <button
                            onClick={logout}
                            style={{
                                background: 'none',
                                border: 'none',
                                color: 'white',
                                cursor: 'pointer',
                                textDecoration: 'underline',
                            }}
                        >
              Logout
            </button>
          </span>
                </h1>
            </header>

            <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

            <main style={styles.main}>
                {children}
            </main>
        </div>
    );
};

// Main App Component
function App() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check for existing session
        const token = localStorage.getItem('token');
        if (token) {
            setUser({ name: 'Admin User', email: 'admin@easybase.com' });
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        // Simulate login - replace with actual API call
        await new Promise(resolve => setTimeout(resolve, 1000));
        const mockToken = 'mock-jwt-token-' + Date.now();
        localStorage.setItem('token', mockToken);
        setUser({ name: 'Admin User', email });
        return true;
    };

    const logout = () => {
        localStorage.removeItem('token');
        setUser(null);
    };

    const authValue = { user, login, logout, loading };

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
                <p>Loading...</p>
            </div>
        );
    }

    return (
        <AuthContext.Provider value={authValue}>
            {user ? (
                <MainLayout>
                    <Dashboard />
                </MainLayout>
            ) : (
                <LoginPage />
            )}
        </AuthContext.Provider>
    );
}

export default App;