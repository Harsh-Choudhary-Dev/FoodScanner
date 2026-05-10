// DOM Content Loaded
document.addEventListener('DOMContentLoaded', function() {
    initThemeToggle();
    initCamera();
    initTabSwitching();
    initManualInput();
    initModal();
    initCameraControls();
});

// Camera functionality
let stream = null;
let isScanning = false;

async function initCamera() {
    const video = document.getElementById('camera');
    const toggleBtn = document.getElementById('toggleCamera');
    
    if (!video) return;

    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        console.error('Media devices API not available.');
        updateStatus('Camera API not available (requires HTTPS)', 'error');
        showNotification('Camera requires a secure context (HTTPS) or localhost.', 'error');
        return;
    }

    try {
        stream = await navigator.mediaDevices.getUserMedia({ 
            video: { facingMode: 'environment' },
            audio: false 
        });
        
        video.srcObject = stream;
        
        // Explicitly try playing the video and catch autoplay blocks
        try {
            await video.play();
        } catch (e) {
            console.error("Autoplay prevented:", e);
        }
        
        updateStatus('Camera ready', 'ready');
        
        if (toggleBtn) {
            toggleBtn.innerHTML = '<i class="fas fa-stop"></i>';
            toggleBtn.style.background = 'var(--red-600)';
            toggleBtn.style.color = 'white';
        }
        
        // Start scanning
        startScanning();
        
    } catch (error) {
        console.error('Camera access error:', error);
        updateStatus('Camera access denied', 'error');
        
        if (error.name === 'NotAllowedError' || error.name === 'PermissionDeniedError') {
            showNotification('Camera access was denied. Please update your browser permissions and reload.', 'error');
        } else {
            showNotification('Could not access the camera. Please check permissions.', 'error');
        }
    }
}

function startScanning() {
    if (isScanning) return;
    isScanning = true;
    
    updateStatus('Focusing camera...', 'searching');
    
    // Take picture after 3 seconds
    setTimeout(async () => {
        if (isScanning) {
            try {
                const blob = await captureImage();
                if (blob) {
                    await analyzeImage(blob);
                } else {
                    updateStatus('Capture failed', 'error');
                    isScanning = false;
                }
            } catch (error) {
                console.error('Capture error:', error);
                isScanning = false;
            }
        }
    }, 3000);
}

function captureImage() {
    const video = document.getElementById('camera');
    const canvas = document.getElementById('canvas');
    if (!video || !stream) return null;
    
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const context = canvas.getContext('2d');
    context.drawImage(video, 0, 0, canvas.width, canvas.height);
    
    return new Promise(resolve => {
        canvas.toBlob(blob => {
            resolve(blob);
        }, 'image/jpeg', 0.8);
    });
}

async function analyzeImage(imageBlob) {
    updateStatus('Analyzing image...', 'searching');
    const formData = new FormData();
    formData.append('image', imageBlob, 'scan.jpg');
    
    try {
        const response = await fetch('/api/v1/analysis/image', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log("Analysis Result:", data);
        
        showScanResult(data);
        updateStatus('Product found!', 'success');
        isScanning = false; // Reset scanning state so user can scan again
        
    } catch (error) {
        console.error('API Error:', error);
        updateStatus('Analysis failed', 'error');
        showNotification('Failed to analyze product. Please try again.', 'error');
        isScanning = false;
    }
}

function stopCamera() {
    const video = document.getElementById('camera');
    const toggleBtn = document.getElementById('toggleCamera');
    
    if (stream) {
        stream.getTracks().forEach(track => track.stop());
        stream = null;
    }
    
    if (video) {
        video.srcObject = null;
    }
    
    isScanning = false;
    updateStatus('Camera stopped', 'stopped');
    
    if (toggleBtn) {
        toggleBtn.innerHTML = '<i class="fas fa-camera"></i>';
        toggleBtn.style.background = 'var(--white)';
        toggleBtn.style.color = 'var(--gray-700)';
    }
}

// Camera controls
function initCameraControls() {
    const toggleBtn = document.getElementById('toggleCamera');
    const switchBtn = document.getElementById('switchCamera');
    const flashBtn = document.getElementById('toggleFlash');
    
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            if (stream) {
                stopCamera();
            } else {
                initCamera();
            }
        });
    }
    
    if (switchBtn) {
        switchBtn.addEventListener('click', () => {
            showNotification('Switch camera feature coming soon!', 'info');
        });
    }
    
    if (flashBtn) {
        flashBtn.addEventListener('click', () => {
            showNotification('Flash feature coming soon!', 'info');
        });
    }
}

// Tab switching for manual input
function initTabSwitching() {
    const tabButtons = document.querySelectorAll('.input-tab');
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

// Manual input functionality
function initManualInput() {
    const barcodeInput = document.getElementById('barcodeInput');
    const productInput = document.getElementById('productInput');
    const searchBarcodeBtn = document.getElementById('searchBarcode');
    const searchProductBtn = document.getElementById('searchProduct');

    if (searchBarcodeBtn && barcodeInput) {
        searchBarcodeBtn.addEventListener('click', () => {
            const barcode = barcodeInput.value.trim();
            if (barcode) {
                searchByBarcode(barcode);
            } else {
                showNotification('Please enter a barcode number', 'error');
            }
        });

        barcodeInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                searchBarcodeBtn.click();
            }
        });
    }

    if (searchProductBtn && productInput) {
        searchProductBtn.addEventListener('click', () => {
            const product = productInput.value.trim();
            if (product) {
                searchByProduct(product);
            } else {
                showNotification('Please enter a product name', 'error');
            }
        });

        productInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                searchProductBtn.click();
            }
        });
    }
}

// Search functions
async function searchByBarcode(barcode) {
    updateStatus('Searching...', 'searching');
    
    try {
        // Simulate API call
        await simulateApiCall(1500);
        
        // Show result
        showScanResult();
        updateStatus('Product found!', 'success');
        
    } catch (error) {
        updateStatus('Search failed', 'error');
        showNotification('Product not found', 'error');
    }
}

async function searchByProduct(productName) {
    updateStatus('Searching...', 'searching');
    
    try {
        // Simulate API call
        await simulateApiCall(1500);
        
        // Show result
        showScanResult();
        updateStatus('Product found!', 'success');
        
    } catch (error) {
        updateStatus('Search failed', 'error');
        showNotification('Product not found', 'error');
    }
}

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

// Modal functionality
function initModal() {
    const modal = document.getElementById('resultModal');
    const closeBtn = document.getElementById('closeModal');
    
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            hideModal();
        });
    }
    
    // Close modal on backdrop click
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                hideModal();
            }
        });
    }
    
    // Close modal on Escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            hideModal();
        }
    });
}

function showScanResult(data) {
    const modal = document.getElementById('resultModal');
    if (modal) {
        if (data) {
            const resultProductName = modal.querySelector('.result-info h3');
            const resultHealthScore = modal.querySelector('.score-value');
            const resultProductImage = modal.querySelector('.result-image img');
            
            if (resultProductName) {
                const name = data.productName || (data.product_details && data.product_details.productName) || 'Unknown Product';
                resultProductName.textContent = name.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
            }
            
            if (resultHealthScore) {
                const score = data.ingredientScore !== undefined ? data.ingredientScore : '--';
                resultHealthScore.textContent = `${score}/100`;
                
                if (score !== '--') {
                    if (score > 70) resultHealthScore.style.color = 'var(--green-600)';
                    else if (score > 40) resultHealthScore.style.color = 'var(--yellow-600)';
                    else resultHealthScore.style.color = 'var(--red-600)';
                }
            }
            
            if (resultProductImage) {
                const images = data.productImage || (data.product_details && data.product_details.productImage);
                if (images && images.length > 0) {
                    resultProductImage.src = images[0].imageUrl;
                }
            }
            
            window.lastScanData = data;
        }

        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function hideModal() {
    const modal = document.getElementById('resultModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = '';
        // Reset to basic view for next time
        setTimeout(() => {
            if (window.showBasicResult) window.showBasicResult();
        }, 300);
    }
}

// Global functions for modal buttons
window.viewDetails = function() {
    if (!window.lastScanData) {
        showNotification('No scan data available.', 'error');
        return;
    }
    
    // Switch Views
    const basicView = document.getElementById('basicResultView');
    const detailView = document.getElementById('detailedAnalysisView');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    if (basicView) basicView.style.display = 'none';
    if (detailView) detailView.style.display = 'block';
    if (modalTitle) modalTitle.textContent = 'Detailed Analysis';
    if (modalContent) modalContent.classList.add('expanded-modal');
    
    // Render Dashboard
    renderAnalysisDashboard(window.lastScanData);
};

window.showBasicResult = function() {
    const basicView = document.getElementById('basicResultView');
    const detailView = document.getElementById('detailedAnalysisView');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    if (basicView) basicView.style.display = 'block';
    if (detailView) detailView.style.display = 'none';
    if (modalTitle) modalTitle.textContent = 'Scan Complete!';
    if (modalContent) modalContent.classList.remove('expanded-modal');
};

window.scanAgain = function() {
    hideModal();
    isScanning = false; // Forcibly reset state
    updateStatus('Ready to scan', 'ready');
    if (!stream) {
        initCamera();
    } else {
        startScanning(); // Ensure we start capturing again if stream is active
    }
};

function renderAnalysisDashboard(data) {
    const dashboard = document.getElementById('analysisDashboardContent');
    if (!dashboard) return;
    
    const details = data.product_details || {};
    const nutrition = details.nutritionFact || {};
    const ingredients = details.ingredients || [];
    
    const formatName = (str) => {
        if (!str) return 'Unknown';
        return str.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    };
    
    const highRiskIngredients = ingredients.filter(i => i.riskLevel === 'high');
    const mediumRiskIngredients = ingredients.filter(i => i.riskLevel === 'medium');
    
    let verdict = "Good Choice";
    let verdictClass = "verdict-success";
    const healthScore = data.ingredientScore !== undefined ? data.ingredientScore : 50;
    
    if (healthScore < 40) {
        verdict = "Not Recommended";
        verdictClass = "verdict-danger";
    } else if (healthScore < 70) {
        verdict = "Consume in Moderation";
        verdictClass = "verdict-warning";
    }

    let servingSizeText = nutrition.serving_size || "100 g";
    let servingSizeNum = parseFloat(servingSizeText) || 100;
    let mult = servingSizeNum / 100;

    const formatNutrient = (val) => val !== undefined && val !== null ? val : 0;
    const calculatePerServing = (val) => (formatNutrient(val) * mult).toFixed(1);

    // Summary Section
    let html = `
        <div class="dashboard-section summary-section">
            <div class="summary-header">
                <div class="score-circle ${verdictClass}">
                    <span class="score-num">${healthScore}</span>
                    <span class="score-max">/100</span>
                </div>
                <div class="summary-info">
                    <h3 class="product-verdict ${verdictClass}">${verdict}</h3>
                    <div class="warnings-summary">
                        ${highRiskIngredients.length > 0 ? `<span class="warning-badge danger"><i class="fas fa-exclamation-triangle"></i> ${highRiskIngredients.length} High Risk</span>` : ''}
                        ${mediumRiskIngredients.length > 0 ? `<span class="warning-badge warning"><i class="fas fa-exclamation-circle"></i> ${mediumRiskIngredients.length} Medium Risk</span>` : ''}
                        ${highRiskIngredients.length === 0 && mediumRiskIngredients.length === 0 ? `<span class="warning-badge success"><i class="fas fa-check-circle"></i> Clean</span>` : ''}
                    </div>
                </div>
            </div>
        </div>
    `;

    // Tabs Header
    html += `
        <div class="analysis-tabs">
            <button class="analysis-tab active" id="tab-btn-nutrition" onclick="switchAnalysisTab('nutrition')">Nutrition</button>
            <button class="analysis-tab" id="tab-btn-ingredients" onclick="switchAnalysisTab('ingredients')">Ingredients</button>
        </div>
    `;

    // Nutrition Tab Content
    html += `
        <div class="analysis-tab-content active" id="tab-content-nutrition">
            <div class="dashboard-section">
                <div class="section-header-flex">
                    <h4 class="section-title mb-0">Nutritional Profile</h4>
                    <span class="serving-badge">Serving: ${servingSizeText}</span>
                </div>
                
                <div class="nutrition-table-container">
                    <table class="nutrition-table">
                        <thead>
                            <tr>
                                <th>Nutrient</th>
                                <th class="text-right">Per Serving</th>
                                <th class="text-right text-muted" style="font-weight: normal; font-size: 0.8rem;">Per 100g</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Calories</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.energy_kcal)} kcal</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.energy_kcal)} kcal</td>
                            </tr>
                            <tr>
                                <td><strong>Protein</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.protein_g)} g</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.protein_g)} g</td>
                            </tr>
                            <tr>
                                <td><strong>Carbs</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.carbohydrates_g)} g</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.carbohydrates_g)} g</td>
                            </tr>
                            <tr>
                                <td><strong>Sugar</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.sugars_g)} g</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.sugars_g)} g</td>
                            </tr>
                            <tr>
                                <td><strong>Fat</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.fat_g)} g</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.fat_g)} g</td>
                            </tr>
                            <tr>
                                <td><strong>Saturated Fat</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.saturated_fat_g)} g</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.saturated_fat_g)} g</td>
                            </tr>
                            <tr>
                                <td><strong>Sodium</strong></td>
                                <td class="text-right val-primary">${calculatePerServing(nutrition.sodium_mg)} mg</td>
                                <td class="text-right text-muted">${formatNutrient(nutrition.sodium_mg)} mg</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;

    // Ingredients Tab Content
    let ingredientsHtml = '<div class="text-muted text-center" style="padding: 20px;">No ingredient data available</div>';
    if (ingredients.length > 0) {
        ingredientsHtml = ingredients.map(ing => {
            const riskClass = ing.riskLevel === 'high' ? 'risk-high' : 
                              (ing.riskLevel === 'medium' ? 'risk-medium' : 'risk-low');
            
            return `
                <div class="ingredient-item ${riskClass}">
                    <div class="ing-header">
                        <span class="ing-name">${formatName(ing.name)}</span>
                        <span class="ing-badge ${riskClass}">${ing.riskLevel || 'ok'}</span>
                    </div>
                    ${ing.description ? `<div class="ing-desc">${ing.description}</div>` : ''}
                </div>
            `;
        }).join('');
    }

    html += `
        <div class="analysis-tab-content" id="tab-content-ingredients" style="display: none;">
            <div class="dashboard-section">
                <div class="overview-grid mb-4">
                    <div class="overview-card">
                        <span class="overview-label">Category</span>
                        <span class="overview-value">${formatName(details.category) || 'N/A'}</span>
                    </div>
                    <div class="overview-card">
                        <span class="overview-label">Brand</span>
                        <span class="overview-value">${details.brand || 'N/A'}</span>
                    </div>
                </div>
                <h4 class="section-title">Ingredients Breakdown</h4>
                <div class="ingredients-list" style="max-height: 400px;">
                    ${ingredientsHtml}
                </div>
            </div>
        </div>
    `;

    dashboard.innerHTML = html;
}

window.switchAnalysisTab = function(tab) {
    document.querySelectorAll('.analysis-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.analysis-tab-content').forEach(c => c.style.display = 'none');
    
    document.getElementById('tab-btn-' + tab).classList.add('active');
    document.getElementById('tab-content-' + tab).style.display = 'block';
};

// Status updates
function updateStatus(text, type = 'ready') {
    const statusText = document.getElementById('statusText');
    const statusIndicator = document.getElementById('statusIndicator');
    
    if (statusText) {
        statusText.textContent = text;
    }
    
    if (statusIndicator) {
        const icon = statusIndicator.querySelector('i');
        const colors = {
            ready: 'var(--green-500)',
            searching: 'var(--yellow-500)',
            success: 'var(--green-500)',
            error: 'var(--red-500)',
            stopped: 'var(--gray-500)'
        };
        
        icon.style.color = colors[type] || colors.ready;
        
        // Add/remove pulse animation
        if (type === 'searching') {
            icon.style.animation = 'pulse 1s infinite';
        } else {
            icon.style.animation = 'none';
        }
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
        info: 'var(--blue-600)',
        warning: 'var(--yellow-600)'
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

// Handle page visibility change
document.addEventListener('visibilitychange', () => {
    if (document.hidden && stream) {
        // Pause camera when page is hidden
        stopCamera();
    } else if (!document.hidden && isScanning) {
        // Resume camera when page becomes visible again
        initCamera();
    }
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (stream) {
        stopCamera();
    }
});
