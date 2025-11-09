import { useEffect } from 'react'

type PageHelmetProps = {
  title: string
  restorePreviousTitle?: boolean
}

const PageHelmet = ({ title, restorePreviousTitle = false }: PageHelmetProps) => {
  useEffect(() => {
    if (!title) {
      return
    }

    const previousTitle = document.title
    document.title = title

    return () => {
      if (restorePreviousTitle) {
        document.title = previousTitle
      }
    }
  }, [title, restorePreviousTitle])

  return null
}

export default PageHelmet

