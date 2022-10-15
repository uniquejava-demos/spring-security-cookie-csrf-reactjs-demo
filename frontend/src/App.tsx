import { useState } from 'react'
import http from './utils/http'

function App() {
  const [authenticated, setAuthenticated] = useState(false)
  const [message, setMessage] = useState('')

  async function onLogin() {
    const params = new URLSearchParams()
    params.append('username', 'cyper')
    params.append('password', 'password')

    const res = await http.post('http://localhost:8080/login', params)
    setAuthenticated(res.data.code === 'success')
    setMessage(res.data.message)
  }

  async function onLogout() {
    await http.post('/logout')
    document.cookie = 'JSESSIONID=; expires=Thu, 01 Jan 1970 00:00:01 GMT;'
  }

  async function onCreateUser() {
    const params = new URLSearchParams()
    params.append('username', 'good-user')
    params.append('password', 'good-password')

    const res = await http.post('http://localhost:8080/admin/users', params)
    if (res.status === 200) {
      alert('User created!')
    } else {
      alert('Create user failed.')
    }
    setMessage(res.data.message)
  }

  return (
    <>
      {authenticated ? (
        <div>
          <button onClick={onCreateUser}>Create user</button>
          <button onClick={onLogout}>Logout</button>
        </div>
      ) : (
        <button onClick={onLogin}>Login</button>
      )}

      <div>Authenticated? {authenticated ? 'true' : 'false'} </div>
      <div>
        Message: <code>{message}</code>
      </div>
    </>
  )
}

export default App
