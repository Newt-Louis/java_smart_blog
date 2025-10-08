document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById('registerForm');
    form.addEventListener('submit', function (e) {
        const phoneHidden = document.getElementById('phone');
        if (itntlTel.isValidNumber()) {
            phoneHidden.value = itntlTel.getNumber(); // E.164
        } else {
            phoneHidden.value = intlTelInputPhone.value;
        }
    });

//     Hidden Register Form Box
    const interactiveButton = document.getElementById('hiddenInteractButton');
    interactiveButton.addEventListener('click', (e) => {
        const registerFormBox = document.getElementById('registerFormBox');
        if(!registerFormBox.classList.contains('d-none')){
            registerFormBox.classList.add('d-none');
        }
    });

//     Show Register Form Box
    const registerNowButton = document.getElementById('nowRegister');
    registerNowButton.addEventListener('click', (e) => {
        const registerFormBox = document.getElementById('registerFormBox');
        if(registerFormBox.classList.contains('d-none')){
            registerFormBox.classList.remove('d-none');
        }
    });
});