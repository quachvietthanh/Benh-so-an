import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthContext } from '../context/AuthContext'
import MainLayout from '../components/layout/MainLayout'
import RoleRoute from '../components/common/RoleRoute'
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
        
        {/* Patient routes - ADMIN, DOCTOR, NURSE, RECEPTIONIST */}
        <Route path="patients" element={
          <RoleRoute roles={['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST']}>
            <PatientList />
          </RoleRoute>
        } />
        <Route path="patients/:id" element={
          <RoleRoute roles={['ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST']}>
            <PatientDetail />
          </RoleRoute>
        } />
        
        {/* Medical Record routes - ADMIN, DOCTOR, NURSE */}
        <Route path="medical-records" element={
          <RoleRoute roles={['ADMIN', 'DOCTOR', 'NURSE']}>
            <MedicalRecordList />
          </RoleRoute>
        } />

        {/* Prescription routes - ADMIN, DOCTOR */}
        <Route path="prescriptions" element={
          <RoleRoute roles={['ADMIN', 'DOCTOR', 'PHARMACIST']}>
            <div>Quản lý đơn thuốc</div>
          </RoleRoute>
        } />

        {/* Appointment routes - ADMIN, DOCTOR, RECEPTIONIST */}
        <Route path="appointments" element={
          <RoleRoute roles={['ADMIN', 'DOCTOR', 'RECEPTIONIST']}>
            <div>Quản lý lịch hẹn</div>
          </RoleRoute>
        } />

        {/* Pharmacy routes - ADMIN, PHARMACIST */}
        <Route path="pharmacy" element={
          <RoleRoute roles={['ADMIN', 'PHARMACIST']}>
            <div>Quản lý nhà thuốc</div>
          </RoleRoute>
        } />

        {/* Invoice routes - ADMIN, RECEPTIONIST */}
        <Route path="invoices" element={
          <RoleRoute roles={['ADMIN', 'RECEPTIONIST']}>
            <div>Quản lý hóa đơn</div>
          </RoleRoute>
        } />
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default AppRoutes
