import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthContext } from '../context/AuthContext'
import MainLayout from '../components/layout/MainLayout'
import Login from '../pages/Login'
import Dashboard from '../pages/Dashboard'
import PatientList from '../pages/PatientList'
import PatientDetail from '../pages/PatientDetail'
import MedicalRecordList from '../pages/MedicalRecordList'
import NotFound from '../pages/NotFound'

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuthContext()

  if (loading) {
    return <div>Loading...</div>
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route path="/" element={
        <PrivateRoute>
          <MainLayout />
        </PrivateRoute>
      }>
        <Route index element={<Dashboard />} />
        <Route path="patients" element={<PatientList />} />
        <Route path="patients/:id" element={<PatientDetail />} />
        <Route path="medical-records" element={<MedicalRecordList />} />
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default AppRoutes
