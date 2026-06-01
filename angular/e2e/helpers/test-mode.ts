export function isRealApi(): boolean {
  return process.env.USE_REAL_API === 'true'
}
