
    function submitForm(formId) {
        var form = document.getElementById(formId);
        if (form) {
            form.submit();
        } else {
            console.error('Form not found with ID:', formId);
        }
    }

    function openChangeNamePopup() {
        var popup = document.getElementById('changeNamePopup');
        if (popup) {
            popup.style.display = "block";
        } else {
            console.error('Popup not found with ID: changeNamePopup');
        }
    }

    function closePopup(popupId) {
        var popup = document.getElementById(popupId);
        if (popup) {
            popup.style.display = "none";
        } else {
            console.error('Popup not found with ID:', popupId);
        }
    }

    function applyChange() {
        // Lógica de la función applyChange
        console.log('applyChange function called');
    }

    function logout() {
        fetch('/api/players/logout', {
            method: 'GET',
            credentials: 'same-origin'
        })
        .then(response => {
            if (response.ok) {
                window.location.href = '/';
            } else {
                console.error('Error al realizar el logout:', response.statusText);
            }
        })
        .catch(error => {
            console.error('Error en la solicitud de logout:', error);
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        // Cierra el popup después de que el documento esté cargado
        closePopup('changeNamePopup');
    });


