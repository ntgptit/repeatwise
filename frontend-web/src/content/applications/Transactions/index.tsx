import PageHeader from './PageHeader'
import PageTitleWrapper from 'src/components/PageTitleWrapper'
import { Grid, Container } from '@mui/material'
import Footer from 'src/components/Footer'

import RecentOrders from './RecentOrders'
import PageHelmet from 'src/components/PageHelmet'

function ApplicationsTransactions() {
  return (
    <>
      <PageHelmet title="Transactions - Applications" />
      <PageTitleWrapper>
        <PageHeader />
      </PageTitleWrapper>
      <Container maxWidth="lg">
        <Grid container direction="row" justifyContent="center" alignItems="stretch" spacing={3}>
          <Grid item xs={12}>
            <RecentOrders />
          </Grid>
        </Grid>
      </Container>
      <Footer />
    </>
  )
}

export default ApplicationsTransactions
