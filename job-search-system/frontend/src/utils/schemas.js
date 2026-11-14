import { z } from 'zod'

export const loginSchema = z.object({
  email: z.string().email('Enter a valid email address'),
  password: z.string().min(1, 'Password is required'),
})

export const registerSchema = z.object({
  username: z.string().trim().min(1, 'Your name is required'),
  email: z.string().email('Enter a valid email address'),
  password: z.string().min(6, 'Use at least 6 characters'),
  role: z.enum(['JOB_SEEKER', 'EMPLOYER']),
})

export const jobSchema = z.object({
  title: z.string().trim().min(1, 'Job title is required'),
  companyName: z.string().trim().optional(),
  location: z.string().trim().optional(),
  description: z.string().trim().optional(),
  skillsRequired: z.string().trim().min(1, 'Add at least one required skill'),
  experienceRequired: z.preprocess(
    (value) => value === '' || value === undefined ? undefined : Number(value),
    z.number().min(0, 'Experience cannot be negative').optional(),
  ),
  salaryRange: z.string().trim().optional(),
  jobType: z.string().optional(),
})

export const seekerProfileSchema = z.object({
  fullName: z.string().trim().min(1, 'Your name is required'),
  email: z.string().email('Enter a valid email address'),
  phone: z.string().optional(),
  location: z.string().optional(),
  skills: z.string().trim().min(1, 'Add at least one skill'),
  experienceYears: z.preprocess(
    (value) => value === '' || value === undefined ? undefined : Number(value),
    z.number().min(0, 'Experience cannot be negative').optional(),
  ),
  summary: z.string().optional(),
})

export const employerProfileSchema = z.object({
  organizationName: z.string().trim().min(1, 'Organization name is required'),
  email: z.string().email('Enter a valid email address'),
  contactNumber: z.string().regex(/^$|^\d{10}$/, 'Enter a 10-digit phone number').optional(),
  location: z.string().optional(),
  about: z.string().optional(),
  website: z.string().optional(),
})
