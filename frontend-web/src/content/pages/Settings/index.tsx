/**
 * Settings Page - combines Profile and Password settings
 */

import { Container, Grid, Typography } from '@mui/material'
import { Helmet } from 'react-helmet-async'
import PageTitleWrapper from 'src/components/PageTitleWrapper'
import ProfileSettings from './ProfileSettings'
import PasswordSettings from './PasswordSettings'

function Settings() {
  return (
    <>
      <Helmet>
        <title>Settings - RepeatWise</title>
      </Helmet>
      <PageTitleWrapper>
        <Typography variant="h3" component="h3" gutterBottom>
          Settings
        </Typography>
        <Typography variant="subtitle2">
          Manage your profile and account settings
        </Typography>
      </PageTitleWrapper>
      <Container maxWidth="lg">
        <Grid
          container
          direction="row"
          justifyContent="center"
          alignItems="stretch"
          spacing={3}
        >
          <Grid item xs={12}>
            <ProfileSettings />
          </Grid>
          <Grid item xs={12}>
            <PasswordSettings />
          </Grid>
        </Grid>
      </Container>
    </>
  )
}

export default Settings
