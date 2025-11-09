import { Box, Container, Card } from '@mui/material'

import { styled } from '@mui/material/styles'
import Logo from 'src/components/LogoSign'
import Hero from './Hero'
import PageHelmet from 'src/components/PageHelmet'

const OverviewWrapper = styled(Box)(
  () => `
    overflow: auto;
    flex: 1;
    overflow-x: hidden;
    align-items: center;
`
)

function Overview() {
  return (
    <OverviewWrapper>
      <PageHelmet title="Tokyo Free White React Typescript Admin Dashboard" />
      <Container maxWidth="lg">
        <Box display="flex" justifyContent="center" py={5} alignItems="center">
          <Logo />
        </Box>
        <Card sx={{ p: 10, mb: 10, borderRadius: 12 }}>
          <Hero />
        </Card>
      </Container>
    </OverviewWrapper>
  )
}

export default Overview
