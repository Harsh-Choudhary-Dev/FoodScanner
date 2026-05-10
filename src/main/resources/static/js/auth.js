// DOM Content Loaded
document.addEventListener('DOMContentLoaded', function() {
    initThemeToggle();
    initTabSwitching();
    initPasswordToggle();
    initFormValidation();
    initPasswordRequirements();
    initSocialAuth();
});

// Theme toggle functionality
function initThemeToggle() {
    const themeToggle = document.getElementById('themeToggle');
    const currentTheme = localStorage.getItem('theme') || 'light';
    
    // Apply saved theme
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon(currentTheme);
    
    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }
}

function updateThemeIcon(theme) {
    const themeToggle = document.getElementById('themeToggle');
    if (themeToggle) {
        const icon = themeToggle.querySelector('i');
        if (theme === 'dark') {
            icon.classList.remove('fa-moon');
            icon.classList.add('fa-sun');
        } else {
            icon.classList.remove('fa-sun');
            icon.classList.add('fa-moon');
        }
    }
}

// Tab switching functionality
function initTabSwitching() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetTab = button.getAttribute('data-tab');
            
            // Remove active class from all tabs and contents
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Add active class to clicked tab and corresponding content
            button.classList.add('active');
            document.getElementById(targetTab).classList.add('active');
        });
    });
}

// Password visibility toggle
function initPasswordToggle() {
    const toggleButtons = document.querySelectorAll('.toggle-password');
    
    toggleButtons.forEach(button => {
        button.addEventListener('click', () => {
            const input = button.previousElementSibling;
            const icon = button.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });
}

// Form validation and submission
function initFormValidation() {
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
}

// Password requirements checker
function initPasswordRequirements() {
    const passwordInput = document.getElementById('signup-password');
    if (!passwordInput) return;

    passwordInput.addEventListener('input', () => {
        const password = passwordInput.value;
        checkPasswordRequirements(password);
    });
}

function checkPasswordRequirements(password) {
    const requirements = {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        number: /\d/.test(password)
    };

    Object.keys(requirements).forEach(req => {
        const element = document.querySelector(`[data-requirement="${req}"]`);
        if (element) {
            if (requirements[req]) {
                element.classList.add('met');
            } else {
                element.classList.remove('met');
            }
        }
    });
}

// Login form handler
async function handleLogin(e) {
    e.preventDefault();
    
    const form = e.target;
    const email = form.querySelector('#login-email').value;
    const password = form.querySelector('#login-password').value;
    const submitBtn = form.querySelector('.btn-auth');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    // Clear previous errors
    clearFormErrors(form);

    // Basic validation
    if (!validateEmail(email)) {
        showError(form.querySelector('#login-email'), 'Please enter a valid email address');
        return;
    }

    if (password.length < 6) {
        showError(form.querySelector('#login-password'), 'Password must be at least 6 characters');
        return;
    }

    // Show loading state
    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'flex';

    try {
        // Simulate API call
        await simulateApiCall(1500);
        
        // Show success message
        showNotification('Login successful! Redirecting...', 'success');
        
        // Simulate redirect
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 2000);
        
    } catch (error) {
        showNotification('Login failed. Please check your credentials.', 'error');
    } finally {
        // Reset button state
        submitBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
}

// Signup form handler
async function handleSignup(e) {
    e.preventDefault();
    
    const form = e.target;
    const name = form.querySelector('#signup-name').value;
    const email = form.querySelector('#signup-email').value;
    const password = form.querySelector('#signup-password').value;
    const confirm = form.querySelector('#signup-confirm').value;
    const terms = form.querySelector('input[name="terms"]').checked;
    const submitBtn = form.querySelector('.btn-auth');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoader = submitBtn.querySelector('.btn-loader');

    // Clear previous errors
    clearFormErrors(form);

    // Validation
    let hasError = false;

    if (name.length < 2) {
        showError(form.querySelector('#signup-name'), 'Name must be at least 2 characters');
        hasError = true;
    }

    if (!validateEmail(email)) {
        showError(form.querySelector('#signup-email'), 'Please enter a valid email address');
        hasError = true;
    }

    if (!validatePassword(password)) {
        showError(form.querySelector('#signup-password'), 'Password does not meet requirements');
        hasError = true;
    }

    if (password !== confirm) {
        showError(form.querySelector('#signup-confirm'), 'Passwords do not match');
        hasError = true;
    }

    if (!terms) {
        showNotification('Please accept the terms of service', 'error');
        hasError = true;
    }

    if (hasError) return;

    // Show loading state
    submitBtn.disabled = true;
    btnText.style.display = 'none';
    btnLoader.style.display = 'flex';

    try {
        // Simulate API call
        await simulateApiCall(2000);
        
        // Show success message
        showNotification('Account created successfully! Redirecting...', 'success');
        
        // Simulate redirect
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 2000);
        
    } catch (error) {
        showNotification('Signup failed. Please try again.', 'error');
    } finally {
        // Reset button state
        submitBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
}

// Validation helpers
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

function validatePassword(password) {
    return password.length >= 8 &&
           /[A-Z]/.test(password) &&
           /[a-z]/.test(password) &&
           /\d/.test(password);
}

function showError(input, message) {
    const errorElement = input.parentElement.nextElementSibling;
    if (errorElement && errorElement.classList.contains('error-message')) {
        errorElement.textContent = message;
        input.style.borderColor = 'var(--red-500)';
    }
}

function clearFormErrors(form) {
    const inputs = form.querySelectorAll('input');
    const errors = form.querySelectorAll('.error-message');
    
    inputs.forEach(input => {
        input.style.borderColor = '';
    });
    
    errors.forEach(error => {
        error.textContent = '';
    });
}

// Social authentication
function initSocialAuth() {
    const googleBtn = document.querySelector('.btn-social.google');
    const appleBtn = document.querySelector('.btn-social.apple');

    if (googleBtn) {
        googleBtn.addEventListener('click', () => {
            showNotification('Google authentication coming soon!', 'info');
        });
    }

    if (appleBtn) {
        appleBtn.addEventListener('click', () => {
            showNotification('Apple authentication coming soon!', 'info');
        });
    }
}

// Notification system
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    
    // Set color based on type
    const colors = {
        success: 'var(--green-600)',
        error: 'var(--red-600)',
        info: 'var(--blue-600)'
    };
    
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${colors[type] || colors.info};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 10000;
        transform: translateX(100%);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-size: 14px;
    `;
    
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Remove after 4 seconds
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 4000);
}

// Simulate API call
function simulateApiCall(delay) {
    return new Promise((resolve) => {
        setTimeout(resolve, delay);
    });
}

// Forgot password handler
document.addEventListener('DOMContentLoaded', function() {
    const forgotPasswordLink = document.querySelector('.forgot-password');
    if (forgotPasswordLink) {
        forgotPasswordLink.addEventListener('click', (e) => {
            e.preventDefault();
            showNotification('Password reset feature coming soon!', 'info');
        });
    }
});
