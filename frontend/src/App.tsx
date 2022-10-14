import { useEffect, useState } from 'react'
import http from './utils/http'

function App() {
  const [authenticated, setAuthenticated] = useState(false)
  const [message, setMessage] = useState('')
  useEffect(() => {
    console.log('useEffect ...')
    const params = new URLSearchParams()
    params.append('username', 'cyper')
    params.append('password', 'password')

    const config = {
      headers: { 'content-type': 'application/x-www-form-urlencoded' },
    }

    const login = async () => {
      const res = await http.post('http://localhost:8080/login', params, config)
      setAuthenticated(res.data.code === 'success')
      setMessage(res.data.message)
    }

    login()
  }, [])

  return (
    <>
      <div>Authenticated? {authenticated ? 'true' : 'false'} </div>
      <div>
        Message: <code>{message}</code>
      </div>
    </>
  )
}

export default App
