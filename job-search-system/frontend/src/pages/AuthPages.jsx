import { zodResolver } from '@hookform/resolvers/zod'
import { ArrowLeft, ArrowRight } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { authApi } from '../api/authApi'
import { getApiError } from '../api/axiosClient'
import { AuthShell } from '../components/layout/AppLayout'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Select } from '../components/ui/Select'
import { useAuth } from '../context/AuthContext'
import { loginSchema, registerSchema } from '../utils/schemas'

export function LoginPage() {
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const [serverError, setServerError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm({ resolver: zodResolver(loginSchema) })

  const submit = async (values) => {
    setSubmitting(true)
    setServerError('')
    try {
      const { data } = await authApi.login(values)
      signIn(data)
      navigate('/dashboard', { replace: true })
    } catch (error) {
      setServerError(getApiError(error))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell eyebrow="Welcome back">
      <Link to="/" className="mb-6 inline-flex items-center gap-1.5 text-sm font-medium text-muted hover:text-olive-800"><ArrowLeft size={15} /> Back home</Link>
      <h1 className="font-display text-4xl font-semibold text-olive-900">Good to see you.</h1>
      <p className="mt-2 text-sm leading-6 text-muted">Sign in to pick up where your next chapter begins.</p>
      {serverError && <p className="mt-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">{serverError}</p>}
      <form className="mt-7 space-y-5" onSubmit={handleSubmit(submit)} noValidate>
        <Input label="Email address" type="email" autoComplete="email" placeholder="you@example.com" error={errors.email?.message} {...register('email')} />
        <Input label="Password" type="password" autoComplete="current-password" placeholder="Your password" error={errors.password?.message} {...register('password')} />
        <Button type="submit" size="lg" loading={submitting} className="w-full">Sign in <ArrowRight size={17} /></Button>
      </form>
      <p className="mt-7 text-center text-sm text-muted">New to Goodwork? <Link to="/auth/register" className="font-semibold text-olive-800 hover:text-olive-600">Create an account</Link></p>
    </AuthShell>
  )
}

export function RegisterPage() {
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const [serverError, setServerError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: searchParams.get('role') === 'EMPLOYER' ? 'EMPLOYER' : 'JOB_SEEKER' },
  })

  const submit = async (values) => {
    setSubmitting(true)
    setServerError('')
    try {
      const { data } = await authApi.register(values)
      signIn(data)
      navigate('/profile?setup=1', { replace: true })
    } catch (error) {
      setServerError(getApiError(error))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell eyebrow="Start with a good step">
      <Link to="/" className="mb-6 inline-flex items-center gap-1.5 text-sm font-medium text-muted hover:text-olive-800"><ArrowLeft size={15} /> Back home</Link>
      <h1 className="font-display text-4xl font-semibold text-olive-900">A good place to begin.</h1>
      <p className="mt-2 text-sm leading-6 text-muted">Create an account and tell us what brings you here.</p>
      {serverError && <p className="mt-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">{serverError}</p>}
      <form className="mt-7 space-y-4" onSubmit={handleSubmit(submit)} noValidate>
        <Input label="Your name" autoComplete="name" placeholder="Alex Morgan" error={errors.username?.message} {...register('username')} />
        <Input label="Email address" type="email" autoComplete="email" placeholder="you@example.com" error={errors.email?.message} {...register('email')} />
        <Input label="Password" type="password" autoComplete="new-password" placeholder="At least 6 characters" error={errors.password?.message} {...register('password')} />
        <Select label="I’m here as a…" error={errors.role?.message} {...register('role')}>
          <option value="JOB_SEEKER">Job seeker</option>
          <option value="EMPLOYER">Employer</option>
        </Select>
        <Button type="submit" size="lg" loading={submitting} className="mt-2 w-full">Create account <ArrowRight size={17} /></Button>
      </form>
      <p className="mt-7 text-center text-sm text-muted">Already have an account? <Link to="/auth/login" className="font-semibold text-olive-800 hover:text-olive-600">Sign in</Link></p>
    </AuthShell>
  )
}
