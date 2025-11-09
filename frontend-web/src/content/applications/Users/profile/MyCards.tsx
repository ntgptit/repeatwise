import { ChangeEvent, useState } from 'react'
import {
  Box,
  Grid,
  Radio,
  FormControlLabel,
  Typography,
  Card,
  CardHeader,
  Divider,
  lighten,
  CardActionArea,
  CardContent,
  Tooltip,
  IconButton,
  Avatar,
  styled,
} from '@mui/material'
import DeleteTwoToneIcon from '@mui/icons-material/DeleteTwoTone'
import AddTwoToneIcon from '@mui/icons-material/AddTwoTone'

const AvatarAddWrapper = styled(Avatar)(
  ({ theme }) => `
        background: ${theme.colors.alpha.black[5]};
        color: ${theme.colors.primary.main};
        width: ${theme.spacing(8)};
        height: ${theme.spacing(8)};
`
)

const CardLogo = styled('img')(
  ({ theme }) => `
      border: 1px solid ${theme.colors.alpha.black[30]};
      border-radius: ${theme.general.borderRadius};
      padding: ${theme.spacing(1)};
      margin-right: ${theme.spacing(2)};
      background: ${theme.colors.alpha.white[100]};
`
)

const CardAddAction = styled(Card)(
  ({ theme }) => `
        border: ${theme.colors.primary.main} dashed 1px;
        height: 100%;
        color: ${theme.colors.primary.main};
        box-shadow: none;
        
        .MuiCardActionArea-root {
          height: 100%;
          justify-content: center;
          align-items: center;
          display: flex;
        }
        
        .MuiTouchRipple-root {
          opacity: .2;
        }
        
        &:hover {
          border-color: ${theme.colors.alpha.black[100]};
        }
`
)

const IconButtonError = styled(IconButton)(
  ({ theme }) => `
     background: ${theme.colors.error.lighter};
     color: ${theme.colors.error.main};
     padding: ${theme.spacing(0.5)};

     &:hover {
      background: ${lighten(theme.colors.error.lighter, 0.4)};
     }
`
)

const CardCc = styled(Card)(
  ({ theme }) => `
     border: 1px solid ${theme.colors.alpha.black[30]};
     background: ${theme.colors.alpha.black[5]};
     box-shadow: none;
`
)

type SavedCard = {
  id: string
  brand: string
  last4: string
  expiry: string
  image: string
}

const initialCards: SavedCard[] = [
  {
    id: 'visa',
    brand: 'Visa',
    last4: '6879',
    expiry: '12/24',
    image: '/static/images/placeholders/logo/visa.png',
  },
  {
    id: 'mastercard',
    brand: 'Mastercard',
    last4: '4634',
    expiry: '6/22',
    image: '/static/images/placeholders/logo/mastercard.png',
  },
]

function MyCards() {
  const [cards, setCards] = useState<SavedCard[]>(initialCards)
  const [selectedValue, setSelectedValue] = useState<string>(initialCards[0]?.id ?? '')

  const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
    setSelectedValue(event.target.value)
  }

  const handleDelete = (cardId: string) => {
    setCards(prevCards => {
      const nextCards = prevCards.filter(card => card.id !== cardId)
      setSelectedValue(prevSelected => {
        if (prevSelected === cardId) {
          return nextCards[0]?.id ?? ''
        }
        return prevSelected
      })
      return nextCards
    })
  }

  return (
    <Card>
      <CardHeader
        subheader={`${cards.length} saved card${cards.length === 1 ? '' : 's'}`}
        title="Cards"
      />
      <Divider />
      <Box p={3}>
        <Grid container spacing={3}>
          {cards.map(card => (
            <Grid item xs={12} sm={6} key={card.id}>
              <CardCc sx={{ px: 2, pt: 2, pb: 1 }}>
                <Box display="flex" alignItems="center">
                  <CardLogo src={card.image} alt={card.brand} />
                  <Box>
                    <Typography variant="h3" fontWeight="normal">
                      •••• {card.last4}
                    </Typography>
                    <Typography variant="subtitle2">
                      Expires:{' '}
                      <Typography component="span" color="text.primary">
                        {card.expiry}
                      </Typography>
                    </Typography>
                  </Box>
                </Box>
                <Box pt={3} display="flex" alignItems="center" justifyContent="space-between">
                  <FormControlLabel
                    value={card.id}
                    control={
                      <Radio
                        checked={selectedValue === card.id}
                        onChange={handleChange}
                        color="primary"
                        name="primary-card"
                      />
                    }
                    label="Primary"
                  />
                  <Tooltip arrow title="Remove this card">
                    <IconButtonError onClick={() => handleDelete(card.id)}>
                      <DeleteTwoToneIcon fontSize="small" />
                    </IconButtonError>
                  </Tooltip>
                </Box>
              </CardCc>
            </Grid>
          ))}
          <Grid item xs={12} sm={6}>
            <Tooltip arrow title="Click to add a new card">
              <CardAddAction>
                <CardActionArea sx={{ px: 1 }}>
                  <CardContent>
                    <AvatarAddWrapper>
                      <AddTwoToneIcon fontSize="large" />
                    </AvatarAddWrapper>
                  </CardContent>
                </CardActionArea>
              </CardAddAction>
            </Tooltip>
          </Grid>
        </Grid>
      </Box>
    </Card>
  )
}

export default MyCards
