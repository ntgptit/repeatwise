import {
  Box,
  Card,
  Typography,
  Container,
  Button,
  TextField,
  FormControl,
  Stack,
} from '@mui/material'
import { styled } from '@mui/material/styles'
import { useState } from 'react'
import PageHelmet from 'src/components/PageHelmet'
import Logo from 'src/components/Logo'

const MainContent = styled(Box)(
  () => `
    height: 100%;
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
`
)

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const handleLogin = () => {
    // TODO: Implement login logic
    console.log('Login clicked', { email, password })
  }

  return (
    <>
      <PageHelmet title="Login" />
      <MainContent>
        <Container maxWidth="sm">
          <Box textAlign="center" mb={3}>
            <Logo />
            <Typography variant="h2" sx={{ my: 2 }}>
              Welcome to RepeatWise
            </Typography>
            <Typography variant="h4" color="text.secondary" fontWeight="normal">
              Sign in to your account
            </Typography>
          </Box>
          <Card sx={{ p: 4 }}>
            <Stack spacing={3}>
              <FormControl fullWidth>
                <TextField
                  label="Email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  fullWidth
                  variant="outlined"
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  fullWidth
                  variant="outlined"
                />
              </FormControl>
              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={handleLogin}
              >
                Sign In
              </Button>
              <Box textAlign="center">
                <Typography variant="body2" color="text.secondary">
                  Don&apos;t have an account?{' '}
                  <Button variant="text" size="small">
                    Sign Up
                  </Button>
                </Typography>
              </Box>
            </Stack>
          </Card>
        </Container>
      </MainContent>
    </>
  )
}

export default Login
