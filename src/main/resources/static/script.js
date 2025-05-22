// script.js
document.addEventListener('DOMContentLoaded', function () {
    // Function to get URL parameters
    function getUrlParameter(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name) || '';
    }

    // Get parameters from URL
    const donorName = getUrlParameter('name') || 'Unknown Donor';
    const phoneNumber = getUrlParameter('phoneNumber') || 'No Phone';
    const bloodType = getUrlParameter('bloodType') || 'Unknown Type';

    // Update the page content with null checks
    const donorNameTitle = document.getElementById('donorNameTitle');
    const donorNameSpan = document.getElementById('donorName');
    const phoneNumberSpan = document.getElementById('phoneNumber');
    const bloodTypeSpan = document.getElementById('bloodType');
    const whatsappLink = document.getElementById('whatsappLink');

    if (donorNameTitle) donorNameTitle.textContent = donorName;
    if (donorNameSpan) donorNameSpan.textContent = donorName;
    if (phoneNumberSpan) phoneNumberSpan.textContent = phoneNumber;
    if (bloodTypeSpan) bloodTypeSpan.textContent = bloodType;

    // Update WhatsApp link
    if (whatsappLink && phoneNumber && phoneNumber !== 'No Phone') {
        // Handle phone number (remove + and any non-digits for WhatsApp)
        const cleanPhone = phoneNumber.replace(/[^\d]/g, '');
        whatsappLink.href = `https://wa.me/${cleanPhone}`;
    }

    // Handle button click
    const showContactBtn = document.getElementById('showContactBtn');
    const contactInfo = document.getElementById('contactInfo');

    if (showContactBtn && contactInfo) {
        showContactBtn.addEventListener('click', function () {
            contactInfo.style.display = 'block';
            showContactBtn.style.display = 'none';
        });
    }

});