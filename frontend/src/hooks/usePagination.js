import { useState } from 'react'

export const usePagination = (initialPage = 0, initialSize = 10) => {
  const [page, setPage] = useState(initialPage)
  const [size, setSize] = useState(initialSize)
  const [total, setTotal] = useState(0)

  const handlePageChange = (newPage) => {
    setPage(newPage)
  }

  const handleSizeChange = (newSize) => {
    setSize(newSize)
    setPage(0)
  }

  const resetPagination = () => {
    setPage(0)
    setSize(initialSize)
    setTotal(0)
  }

  return {
    page,
    size,
    total,
    setTotal,
    handlePageChange,
    handleSizeChange,
    resetPagination,
  }
}
