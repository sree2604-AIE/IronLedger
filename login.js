/* ═══════════════════════════════════════════════════════════════
   IronLedger — Login Form Validation
   File: login.js
═══════════════════════════════════════════════════════════════ */

const form          = document.getElementById('loginForm');
const usernameEl    = document.getElementById('username');
const passwordEl    = document.getElementById('password');
const loginBtn      = document.getElementById('loginBtn');
const successBanner = document.getElementById('successBanner');
const togglePwBtn   = document.getElementById('togglePw');
const eyeShow       = document.getElementById('eyeShow');
const eyeHide       = document.getElementById('eyeHide');

/* ── SVG Icons ─────────────────────────────────────────────────── */
const ICON_ERR = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
  stroke-linecap="round" stroke-linejoin="round">
  <circle cx="12" cy="12" r="10"/>
  <line x1="12" y1="8" x2="12" y2="12"/>
  <line x1="12" y1="16" x2="12.01" y2="16"/>
</svg>`;

const ICON_OK = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
  stroke-linecap="round" stroke-linejoin="round">
  <polyline points="20 6 9 17 4 12"/>
</svg>`;

/* ── Strength Bar Colours ──────────────────────────────────────── */
const STRENGTH_COLORS = ['#fc8181', '#f6ad55', '#68d391', '#48c78e'];

/* ── Helpers ───────────────────────────────────────────────────── */

/**
 * Display an error or success message below a field.
 * @param {HTMLElement} el   - The .field-msg element
 * @param {'err'|'ok'} type  - Message type
 * @param {string} text      - Message text
 */
function showMsg(el, type, text) {
  el.innerHTML = `${type === 'err' ? ICON_ERR : ICON_OK} ${text}`;
  el.className = `field-msg ${type} show`;
}

/**
 * Clear any message below a field.
 * @param {HTMLElement} el - The .field-msg element
 */
function clearMsg(el) {
  el.className = 'field-msg';
  el.innerHTML = '';
}

/**
 * Toggle the visual state of an input (error / valid / neutral).
 * @param {HTMLInputElement} input
 * @param {'error'|'valid'|null} state
 */
function setInputState(input, state) {
  input.classList.remove('is-error', 'is-valid');
  if (state === 'error') input.classList.add('is-error');
  if (state === 'valid') input.classList.add('is-valid');
}

/* ── Password Strength ─────────────────────────────────────────── */

/**
 * Calculate a strength score 0-4 based on password composition.
 * @param {string} pw
 * @returns {number}
 */
function calcStrength(pw) {
  let score = 0;
  if (pw.length >= 8)          score++;  // length
  if (/[A-Z]/.test(pw))        score++;  // uppercase
  if (/[0-9]/.test(pw))        score++;  // digit
  if (/[^A-Za-z0-9]/.test(pw)) score++;  // special char
  return score;
}

/**
 * Update the 4-segment strength bar UI.
 * @param {string} pw
 */
function updateStrengthBar(pw) {
  const score = calcStrength(pw);
  const segs  = [
    document.getElementById('seg1'),
    document.getElementById('seg2'),
    document.getElementById('seg3'),
    document.getElementById('seg4')
  ];
  const color = pw.length === 0
    ? null
    : STRENGTH_COLORS[score - 1] || STRENGTH_COLORS[0];

  segs.forEach((s, i) => {
    s.style.background = (pw.length > 0 && i < score)
      ? color
      : 'rgba(255,255,255,0.08)';
  });
}

/* ── Validation Functions ──────────────────────────────────────── */

/**
 * Validate the username field.
 * @param {boolean} showOk - Whether to mark the field green on success
 * @returns {boolean}
 */
function validateUsername(showOk = false) {
  const val   = usernameEl.value.trim();
  const msgEl = document.getElementById('username-msg');

  if (val === '') {
    showMsg(msgEl, 'err', 'Username cannot be empty.');
    setInputState(usernameEl, 'error');
    return false;
  }

  clearMsg(msgEl);
  setInputState(usernameEl, showOk ? 'valid' : null);
  return true;
}

/**
 * Validate the password field.
 * @param {boolean} showOk - Whether to mark the field green on success
 * @returns {boolean}
 */
function validatePassword(showOk = false) {
  const val   = passwordEl.value;
  const msgEl = document.getElementById('password-msg');

  if (val === '') {
    showMsg(msgEl, 'err', 'Password cannot be empty.');
    setInputState(passwordEl, 'error');
    return false;
  }

  if (val.length < 8) {
    showMsg(msgEl, 'err', `Password must be at least 8 characters (${val.length}/8).`);
    setInputState(passwordEl, 'error');
    return false;
  }

  clearMsg(msgEl);
  setInputState(passwordEl, showOk ? 'valid' : null);
  return true;
}

/* ── Event Listeners ───────────────────────────────────────────── */

// Validate on leaving the field
usernameEl.addEventListener('blur', () => validateUsername());
passwordEl.addEventListener('blur', () => validatePassword());

// Clear error while user re-types
usernameEl.addEventListener('input', () => {
  if (usernameEl.classList.contains('is-error')) {
    clearMsg(document.getElementById('username-msg'));
    setInputState(usernameEl, null);
  }
});

passwordEl.addEventListener('input', () => {
  updateStrengthBar(passwordEl.value);
  if (passwordEl.classList.contains('is-error')) {
    clearMsg(document.getElementById('password-msg'));
    setInputState(passwordEl, null);
  }
});

// Show / hide password toggle
togglePwBtn.addEventListener('click', () => {
  const isHidden        = passwordEl.type === 'password';
  passwordEl.type       = isHidden ? 'text'     : 'password';
  eyeShow.style.display = isHidden ? 'none'     : '';
  eyeHide.style.display = isHidden ? ''         : 'none';
  togglePwBtn.setAttribute('aria-label', isHidden ? 'Hide password' : 'Show password');
});

// Form submit handler
form.addEventListener('submit', async (e) => {
  e.preventDefault();

  // Hide any existing success banner
  successBanner.classList.remove('show');

  const okUser = validateUsername(true);
  const okPass = validatePassword(true);

  // Stop if any validation failed
  if (!okUser || !okPass) return;

  // Simulate async login request (spinner shown)
  loginBtn.disabled = true;
  loginBtn.classList.add('loading');
  await new Promise(resolve => setTimeout(resolve, 1200));
  loginBtn.classList.remove('loading');
  loginBtn.disabled = false;

  // Show success banner — "Login Successful"
  successBanner.classList.add('show');

  // Clear the form after a short delay
  setTimeout(() => {
    form.reset();
    setInputState(usernameEl, null);
    setInputState(passwordEl, null);
    clearMsg(document.getElementById('username-msg'));
    clearMsg(document.getElementById('password-msg'));
    updateStrengthBar('');

    // Auto-hide banner after 3.5 s
    setTimeout(() => successBanner.classList.remove('show'), 3500);
  }, 1800);
});

// Prevent default on signup link (placeholder)
document.getElementById('signupLink').addEventListener('click', e => e.preventDefault());
