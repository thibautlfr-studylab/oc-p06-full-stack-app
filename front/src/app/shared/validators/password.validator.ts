/**
 * Password validation pattern.
 *
 * Regex breakdown:
 * ^                 - Start of string
 * (?=.*[0-9])       - At least 1 digit
 * (?=.*[a-z])       - At least 1 lowercase
 * (?=.*[A-Z])       - At least 1 uppercase
 * (?=.*[@#$%^&+=!]) - At least 1 special char
 * (?=\S+$)          - No whitespace
 * .{8,}             - Min 8 characters
 * $                 - End of string
 */
export const PASSWORD_PATTERN = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/;

/**
 * Returns detailed error messages for password validation.
 */
export function getPasswordErrors(password: string): string[] {
  const errors: string[] = [];

  if (password.length < 8) {
    errors.push('Au moins 8 caracteres');
  }
  if (!/[0-9]/.test(password)) {
    errors.push('Au moins 1 chiffre');
  }
  if (!/[a-z]/.test(password)) {
    errors.push('Au moins 1 minuscule');
  }
  if (!/[A-Z]/.test(password)) {
    errors.push('Au moins 1 majuscule');
  }
  if (!/[@#$%^&+=!]/.test(password)) {
    errors.push('Au moins 1 caractere special (@#$%^&+=!)');
  }

  return errors;
}
