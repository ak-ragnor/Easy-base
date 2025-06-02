import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useLocation } from 'react-router-dom';

// Simple API Service
const ApiService = {
    baseURL: '/easy-base/api',

    async get(endpoint) {
        try {
            const response = await fetch(`${this.baseURL}${endpoint}`);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`API GET ${endpoint} failed:`, error);
            throw error;
        }
    }
};

// Dashboard Component
function Dashboard() {
    const [systemStatus, setSystemStatus] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                console.log('Loading dashboard data...');
                const healthData = await ApiService.get('/admin/health');
                const infoData = await ApiService.get('/admin/info');

                setSystemStatus({
                    health: healthData,
                    info: infoData,
                    lastUpdated: new Date().toLocaleString()
                });
            } catch (error) {
                console.error('Failed to load dashboard data:', error);
                setSystemStatus({
                    error: 'Failed to load system status',
                    lastUpdated: new Date().toLocaleString()
                });
            } finally {
                setLoading(false);
            }
        };

        loadData();
        const interval = setInterval(loadData, 30000);
        return () => clearInterval(interval);
    }, []);

    if (loading) {
        return (
            <div style={styles.loadingContainer}>
                <div style={styles.spinner}></div>
                <div>Loading dashboard...</div>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>System Dashboard</h1>

            <div style={styles.grid}>
                <div style={styles.card}>
                    <h3>System Status</h3>
                    {systemStatus?.error ? (
                        <div style={{ color: 'red' }}>{systemStatus.error}</div>
                    ) : (
                        <div>
                            <div style={styles.statusItem}>
                                Status: <strong style={{ color: 'green' }}>
                                {systemStatus?.health?.status || 'Unknown'}
                            </strong>
                            </div>
                            <div style={styles.statusItem}>
                                Application: {systemStatus?.health?.application}
                            </div>
                            <div style={styles.statusItem}>
                                Version: {systemStatus?.health?.version}
                            </div>
                            <div style={styles.statusItem}>
                                Profile: {systemStatus?.health?.profile}
                            </div>
                        </div>
                    )}
                    <div style={styles.timestamp}>
                        Last updated: {systemStatus?.lastUpdated}
                    </div>
                </div>

                <div style={styles.card}>
                    <h3>Application Info</h3>
                    <div style={styles.statusItem}>
                        Java Version: {systemStatus?.info?.['java.version']}
                    </div>
                    <div style={styles.statusItem}>
                        Context Path: {systemStatus?.info?.['context.path']}
                    </div>
                    <div style={styles.statusItem}>
                        Build Time: {systemStatus?.info?.['build.timestamp']}
                    </div>
                </div>

                <div style={styles.card}>
                    <h3>Memory Usage</h3>
                    {systemStatus?.health?.memory ? (
                        <div>
                            <div style={styles.statusItem}>Total: {systemStatus.health.memory.total}</div>
                            <div style={styles.statusItem}>Used: {systemStatus.health.memory.used}</div>
                            <div style={styles.statusItem}>Free: {systemStatus.health.memory.free}</div>
                            <div style={styles.statusItem}>Max: {systemStatus.health.memory.max}</div>
                        </div>
                    ) : (
                        <div>Memory info not available</div>
                    )}
                </div>

                <div style={styles.card}>
                    <h3>Quick Actions</h3>
                    <div style={styles.buttonGroup}>
                        <button style={styles.button}>View System Logs</button>
                        <button style={styles.button}>Database Status</button>
                        <button style={styles.button}>API Documentation</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

// Users Component
function Users() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadUsers = async () => {
            try {
                console.log('Loading users...');
                const userData = await ApiService.get('/users');
                setUsers(userData.content || userData || []);
            } catch (error) {
                console.error('Failed to load users:', error);
                // Mock data fallback
                setUsers([
                    { id: 1, name: 'John Doe', email: 'john@example.com', role: 'Admin', status: 'Active' },
                    { id: 2, name: 'Jane Smith', email: 'jane@example.com', role: 'User', status: 'Active' },
                    { id: 3, name: 'Bob Johnson', email: 'bob@example.com', role: 'Manager', status: 'Active' },
                ]);
            } finally {
                setLoading(false);
            }
        };

        loadUsers();
    }, []);

    if (loading) {
        return (
            <div style={styles.loadingContainer}>
                <div style={styles.spinner}></div>
                <div>Loading users...</div>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>User Management</h1>

            <div style={{ marginBottom: '20px' }}>
                <button style={styles.primaryButton}>Add New User</button>
            </div>

            <div style={styles.userList}>
                {users.map((user, index) => (
                    <div key={user.id} style={{
                        ...styles.userItem,
                        borderBottom: index < users.length - 1 ? '1px solid #eee' : 'none'
                    }}>
                        <div style={styles.userInfo}>
                            <div style={styles.avatar}>
                                {user.name.charAt(0)}
                            </div>
                            <div>
                                <div style={styles.userName}>{user.name}</div>
                                <div style={styles.userDetails}>{user.email} • {user.role}</div>
                                {user.status && (
                                    <div style={styles.userStatus}>Status: {user.status}</div>
                                )}
                            </div>
                        </div>
                        <div style={styles.userActions}>
                            <button style={styles.actionButton}>Edit</button>
                            <button style={{...styles.actionButton, ...styles.dangerButton}}>Delete</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

// Navigation Component
function Navigation({ open, onToggle }) {
    const location = useLocation();

    const menuItems = [
        { text: 'Dashboard', path: '/dashboard' },
        { text: 'Users', path: '/users' },
        { text: 'Settings', path: '/settings' },
    ];

    if (!open) return null;

    return (
        <>
            <div style={styles.drawer}>
                {menuItems.map((item) => (
                    <Link
                        key={item.text}
                        to={item.path}
                        onClick={onToggle}
                        style={{
                            ...styles.navItem,
                            ...(location.pathname === item.path ? styles.navItemActive : {})
                        }}
                    >
                        {item.text}
                    </Link>
                ))}
            </div>
            <div style={styles.overlay} onClick={onToggle} />
        </>
    );
}

// Main App Component
function App() {
    const [drawerOpen, setDrawerOpen] = useState(false);

    const handleDrawerToggle = () => {
        setDrawerOpen(!drawerOpen);
    };

    // Hide loading indicator when React loads
    useEffect(() => {
        console.log('React App loaded successfully!');
        if (window.hideLoadingIndicator) {
            window.hideLoadingIndicator();
        }
    }, []);

    return (
        <Router basename="/easy-base">
            <div style={styles.appContainer}>
                {/* App Bar */}
                <div style={styles.appBar}>
                    <button onClick={handleDrawerToggle} style={styles.menuButton}>
                        ☰
                    </button>
                    <h2 style={styles.appTitle}>Easy Base Enterprise</h2>
                    <div style={styles.appActions}>
                        <button style={styles.iconButton}>🔔</button>
                        <button style={styles.iconButton}>👤</button>
                    </div>
                </div>

                {/* Navigation */}
                <Navigation open={drawerOpen} onToggle={handleDrawerToggle} />

                {/* Main Content */}
                <div style={styles.mainContent}>
                    <Routes>
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/users" element={<Users />} />
                        <Route path="/settings" element={
                            <div style={styles.container}>
                                <h1 style={styles.title}>Settings</h1>
                                <p>Settings page coming soon...</p>
                            </div>
                        } />
                        <Route path="*" element={
                            <div style={styles.container}>
                                <h1 style={styles.title}>Page Not Found</h1>
                                <p>The page you're looking for doesn't exist.</p>
                                <Link to="/dashboard" style={styles.primaryButton}>
                                    Go to Dashboard
                                </Link>
                            </div>
                        } />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

// Styles
const styles = {
    appContainer: {
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", sans-serif'
    },
    appBar: {
        background: '#1976d2',
        color: 'white',
        padding: '0 20px',
        height: '60px',
        display: 'flex',
        alignItems: 'center',
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        zIndex: 1100,
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    },
    menuButton: {
        background: 'none',
        border: 'none',
        color: 'white',
        marginRight: '20px',
        cursor: 'pointer',
        fontSize: '18px',
        padding: '8px'
    },
    appTitle: {
        margin: 0,
        flexGrow: 1,
        fontSize: '20px',
        fontWeight: '500'
    },
    appActions: {
        display: 'flex',
        gap: '10px'
    },
    iconButton: {
        background: 'none',
        border: 'none',
        color: 'white',
        cursor: 'pointer',
        fontSize: '18px',
        padding: '8px'
    },
    drawer: {
        position: 'fixed',
        top: '60px',
        left: 0,
        width: '240px',
        height: 'calc(100vh - 60px)',
        background: 'white',
        boxShadow: '2px 0 4px rgba(0,0,0,0.1)',
        zIndex: 1000,
        padding: '20px 0'
    },
    overlay: {
        position: 'fixed',
        top: '60px',
        left: 0,
        right: 0,
        bottom: 0,
        background: 'rgba(0,0,0,0.5)',
        zIndex: 999
    },
    navItem: {
        display: 'block',
        padding: '12px 20px',
        textDecoration: 'none',
        color: '#333',
        fontWeight: '400'
    },
    navItemActive: {
        color: '#1976d2',
        background: '#f5f5f5',
        fontWeight: '500'
    },
    mainContent: {
        flexGrow: 1,
        background: '#f5f5f5',
        minHeight: '100vh',
        paddingTop: '60px'
    },
    container: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '20px'
    },
    title: {
        marginBottom: '20px',
        color: '#1976d2',
        fontSize: '28px',
        fontWeight: '500'
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: '20px'
    },
    card: {
        background: 'white',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    },
    statusItem: {
        marginBottom: '8px',
        color: '#333'
    },
    timestamp: {
        fontSize: '12px',
        color: '#666',
        marginTop: '15px'
    },
    buttonGroup: {
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
    },
    button: {
        padding: '10px',
        border: '1px solid #1976d2',
        background: 'white',
        color: '#1976d2',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    primaryButton: {
        padding: '12px 24px',
        background: '#1976d2',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '14px',
        fontWeight: '500',
        textDecoration: 'none',
        display: 'inline-block'
    },
    loadingContainer: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '400px',
        gap: '20px'
    },
    spinner: {
        width: '40px',
        height: '40px',
        border: '3px solid #f3f3f3',
        borderTop: '3px solid #1976d2',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite'
    },
    userList: {
        background: 'white',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    },
    userItem: {
        padding: '15px 20px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
    },
    userInfo: {
        display: 'flex',
        alignItems: 'center'
    },
    avatar: {
        width: '40px',
        height: '40px',
        borderRadius: '50%',
        background: '#1976d2',
        color: 'white',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginRight: '15px',
        fontSize: '16px',
        fontWeight: 'bold'
    },
    userName: {
        fontWeight: '500',
        marginBottom: '4px'
    },
    userDetails: {
        color: '#666',
        fontSize: '14px'
    },
    userStatus: {
        color: '#666',
        fontSize: '12px',
        marginTop: '2px'
    },
    userActions: {
        display: 'flex',
        gap: '10px'
    },
    actionButton: {
        padding: '6px 12px',
        border: '1px solid #1976d2',
        background: 'white',
        color: '#1976d2',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '12px'
    },
    dangerButton: {
        borderColor: '#d32f2f',
        color: '#d32f2f'
    }
};

export default App;