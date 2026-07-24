import test from 'node:test'
import assert from 'node:assert/strict'

/**
 * Helper: Kiểm tra tính hợp lệ khi cập nhật chỉ số sinh tồn và hồ sơ bệnh án
 */
function validateMedicalRecordUpdate(payload) {
  const errors = {}

  if (!payload.diagnosis || !payload.diagnosis.trim()) {
    errors.diagnosis = 'Vui lòng nhập chẩn đoán bệnh'
  }

  if (payload.bloodPressure && !/^\d{2,3}\/\d{2,3}$/.test(payload.bloodPressure.trim())) {
    errors.bloodPressure = 'Huyết áp phải có dạng Tâm thu/Tâm trương (VD: 120/80)'
  }

  if (payload.heartRate !== undefined && payload.heartRate !== null) {
    const hr = Number(payload.heartRate)
    if (isNaN(hr) || hr < 30 || hr > 220) {
      errors.heartRate = 'Nhịp tim không hợp lệ (30 - 220 bpm)'
    }
  }

  if (payload.temperature !== undefined && payload.temperature !== null) {
    const temp = Number(payload.temperature)
    if (isNaN(temp) || temp < 34.0 || temp > 43.0) {
      errors.temperature = 'Thân nhiệt không hợp lệ (34.0°C - 43.0°C)'
    }
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  }
}

/**
 * Helper: Tạo payload cập nhật hồ sơ bệnh án PUT /medical-records/{id}
 */
function buildMedicalRecordUpdatePayload(input) {
  return {
    symptoms: input.symptoms?.trim() || '',
    diagnosis: input.diagnosis?.trim(),
    treatmentPlan: input.treatmentPlan?.trim() || '',
    vitalSigns: {
      bloodPressure: input.bloodPressure?.trim() || null,
      heartRate: input.heartRate ? Number(input.heartRate) : null,
      temperature: input.temperature ? Number(input.temperature) : null,
    },
    notes: input.notes?.trim() || null,
  }
}

test('Kiểm thử cập nhật hồ sơ: Bắt buộc chẩn đoán bệnh', () => {
  const result = validateMedicalRecordUpdate({
    symptoms: 'Ho, sốt nhẹ',
    diagnosis: '',
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.diagnosis, 'Vui lòng nhập chẩn đoán bệnh')
})

test('Kiểm thử cập nhật hồ sơ: Kiểm tra định dạng huyết áp và thân nhiệt', () => {
  const result = validateMedicalRecordUpdate({
    diagnosis: 'Viêm phế quản cấp',
    bloodPressure: '120-80', // Sai định dạng
    temperature: 45.0, // Sai thân nhiệt
  })
  assert.equal(result.isValid, false)
  assert.equal(result.errors.bloodPressure, 'Huyết áp phải có dạng Tâm thu/Tâm trương (VD: 120/80)')
  assert.equal(result.errors.temperature, 'Thân nhiệt không hợp lệ (34.0°C - 43.0°C)')
})

test('Kiểm thử cập nhật hồ sơ: Chuẩn hóa dữ liệu cập nhật bệnh án hợp lệ', () => {
  const input = {
    symptoms: ' Đau họng, sốt 38 độ ',
    diagnosis: ' Viêm họng cấp ',
    treatmentPlan: ' Uống kháng sinh 5 ngày ',
    bloodPressure: ' 120/80 ',
    heartRate: ' 75 ',
    temperature: ' 38.2 ',
    notes: ' Nghỉ ngơi nhiều ',
  }

  const validation = validateMedicalRecordUpdate(input)
  assert.equal(validation.isValid, true)

  const payload = buildMedicalRecordUpdatePayload(input)
  assert.deepEqual(payload, {
    symptoms: 'Đau họng, sốt 38 độ',
    diagnosis: 'Viêm họng cấp',
    treatmentPlan: 'Uống kháng sinh 5 ngày',
    vitalSigns: {
      bloodPressure: '120/80',
      heartRate: 75,
      temperature: 38.2,
    },
    notes: 'Nghỉ ngơi nhiều',
  })
})
