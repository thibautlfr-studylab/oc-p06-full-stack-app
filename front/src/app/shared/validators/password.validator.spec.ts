import { PASSWORD_PATTERN, getPasswordErrors } from './password.validator';

describe('PasswordValidator', () => {

  describe('PASSWORD_PATTERN', () => {

    it('should match a valid password', () => {
      const validPassword = 'Password1!';
      expect(PASSWORD_PATTERN.test(validPassword)).toBeTrue();
    });

    it('should not match password without digit', () => {
      const password = 'Password!';
      expect(PASSWORD_PATTERN.test(password)).toBeFalse();
    });

    it('should not match password without lowercase', () => {
      const password = 'PASSWORD1!';
      expect(PASSWORD_PATTERN.test(password)).toBeFalse();
    });

    it('should not match password without uppercase', () => {
      const password = 'password1!';
      expect(PASSWORD_PATTERN.test(password)).toBeFalse();
    });

    it('should not match password without special character', () => {
      const password = 'Password1';
      expect(PASSWORD_PATTERN.test(password)).toBeFalse();
    });

    it('should not match password shorter than 8 characters', () => {
      const password = 'Pass1!';
      expect(PASSWORD_PATTERN.test(password)).toBeFalse();
    });

  });

  describe('getPasswordErrors', () => {

    it('should return empty array for valid password', () => {
      const errors = getPasswordErrors('Password1!');
      expect(errors).toEqual([]);
    });

    it('should return error for password shorter than 8 characters', () => {
      const errors = getPasswordErrors('Pass1!');
      expect(errors).toContain('Au moins 8 caracteres');
    });

    it('should return error for password without digit', () => {
      const errors = getPasswordErrors('Password!');
      expect(errors).toContain('Au moins 1 chiffre');
    });

    it('should return error for password without lowercase', () => {
      const errors = getPasswordErrors('PASSWORD1!');
      expect(errors).toContain('Au moins 1 minuscule');
    });

    it('should return error for password without uppercase', () => {
      const errors = getPasswordErrors('password1!');
      expect(errors).toContain('Au moins 1 majuscule');
    });

    it('should return error for password without special character', () => {
      const errors = getPasswordErrors('Password1');
      expect(errors).toContain('Au moins 1 caractere special (@#$%^&+=!)');
    });

    it('should return multiple errors for password with multiple issues', () => {
      const errors = getPasswordErrors('abc');
      expect(errors.length).toBeGreaterThan(1);
      expect(errors).toContain('Au moins 8 caracteres');
      expect(errors).toContain('Au moins 1 chiffre');
      expect(errors).toContain('Au moins 1 majuscule');
      expect(errors).toContain('Au moins 1 caractere special (@#$%^&+=!)');
    });

    it('should return all 5 errors for empty string', () => {
      const errors = getPasswordErrors('');
      expect(errors.length).toBe(5);
    });

  });

});
