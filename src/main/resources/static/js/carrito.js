// Función para mostrar alertas
function mostrarAlerta(mensaje, tipo) {
    const alertaHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3"
             role="alert" style="z-index: 9999; min-width: 300px;">
            <i class="fas fa-${tipo === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    document.body.insertAdjacentHTML('afterbegin', alertaHTML);

    setTimeout(() => {
        const alerta = document.querySelector('.alert');
        if (alerta) alerta.remove();
    }, 3000);
}

// Función para verificar si hay sesión
function verificarSesion() {
    const usuarioLogueado = document.querySelector('#dropdownMenuButton');
    return usuarioLogueado !== null;
}

// Función para cargar datos del carrito
function cargarCarrito() {
    if (!verificarSesion()) {
        document.getElementById('carritoSinSesion').style.display = 'block';
        document.getElementById('carritoVacio').style.display = 'none';
        document.getElementById('carritoContenido').style.display = 'none';
        return;
    }

    document.getElementById('carritoSinSesion').style.display = 'none';

    fetch('/carrito/datos')
        .then(response => response.json())
        .then(data => {
            if (!data.items || data.items.length === 0) {
                document.getElementById('carritoVacio').style.display = 'block';
                document.getElementById('carritoContenido').style.display = 'none';
                actualizarContadorCarrito(0);
            } else {
                document.getElementById('carritoVacio').style.display = 'none';
                document.getElementById('carritoContenido').style.display = 'block';
                mostrarProductosCarrito(data.items);
                document.getElementById('totalCarrito').textContent = data.total.toFixed(2);

                const totalItems = data.items.reduce((sum, item) => sum + item.cantidad, 0);
                actualizarContadorCarrito(totalItems);
            }
        })
        .catch(error => {
            document.getElementById('carritoVacio').style.display = 'block';
            document.getElementById('carritoContenido').style.display = 'none';
        });
}

// Función para mostrar productos en el carrito
function mostrarProductosCarrito(items) {
    const listaProductos = document.getElementById('listaProductosCarrito');
    listaProductos.innerHTML = '';

    items.forEach(item => {
        const productoHTML = `
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-3">
                            <img src="${item.producto.imagen}"
                                 alt="${item.producto.nombre}"
                                 class="img-fluid rounded"
                                 style="max-height: 80px; object-fit: contain;">
                        </div>
                        <div class="col-9">
                            <h6 class="mb-1">${item.producto.nombre}</h6>
                            <p class="text-primary fw-bold mb-2">S/ ${item.producto.precio.toFixed(2)}</p>
                            <div class="d-flex align-items-center justify-content-between">
                                <div class="input-group" style="max-width: 120px;">
                                    <button class="btn btn-outline-secondary btn-sm"
                                            onclick="cambiarCantidad(${item.id}, ${item.cantidad - 1})">
                                        <i class="fas fa-minus"></i>
                                    </button>
                                    <input type="text"
                                           class="form-control form-control-sm text-center"
                                           value="${item.cantidad}"
                                           readonly>
                                    <button class="btn btn-outline-secondary btn-sm"
                                            onclick="cambiarCantidad(${item.id}, ${item.cantidad + 1})">
                                        <i class="fas fa-plus"></i>
                                    </button>
                                </div>
                                <button class="btn btn-danger btn-sm" onclick="eliminarDelCarrito(${item.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        listaProductos.insertAdjacentHTML('beforeend', productoHTML);
    });
}

// Función para actualizar el contador del carrito
function actualizarContadorCarrito(cantidad) {
    const badge = document.getElementById('cantidadCarrito');
    if (cantidad > 0) {
        badge.textContent = cantidad;
        badge.style.display = 'inline-block';
    } else {
        badge.style.display = 'none';
    }
}

// Función para agregar producto al carrito
function agregarAlCarrito(productoId) {
    if (!verificarSesion()) {
        mostrarAlerta('Debes iniciar sesión para agregar productos al carrito', 'warning');
        const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
        loginModal.show();
        return;
    }

    const formData = new FormData();
    formData.append('productoId', productoId);
    formData.append('cantidad', 1);

    fetch('/carrito/agregar', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            mostrarAlerta(data.message, 'success');
            actualizarContadorCarrito(data.cantidadItems);
        } else {
            mostrarAlerta(data.message, 'danger');
        }
    })
    .catch(error => {
        mostrarAlerta('Error al agregar el producto', 'danger');
    });
}

// Función para eliminar producto del carrito
function eliminarDelCarrito(itemId) {
    fetch(`/carrito/eliminar/${itemId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cargarCarrito();
            mostrarAlerta(data.message, 'success');
        } else {
            mostrarAlerta(data.message, 'danger');
        }
    })
    .catch(error => {
        mostrarAlerta('Error al eliminar el producto', 'danger');
    });
}

// Función para cambiar cantidad
function cambiarCantidad(itemId, nuevaCantidad) {
    if (nuevaCantidad < 1) {
        eliminarDelCarrito(itemId);
        return;
    }

    const formData = new FormData();
    formData.append('cantidad', nuevaCantidad);

    fetch(`/carrito/actualizar/${itemId}`, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            cargarCarrito();
        } else {
            mostrarAlerta(data.message, 'danger');
        }
    })
    .catch(error => {
        mostrarAlerta('Error al actualizar la cantidad', 'danger');
    });
}

// Función para finalizar compra - CORREGIDA COMPLETAMENTE
function finalizarCompra() {
    // Verificar que el modal existe en el DOM
    const modalElement = document.getElementById('resumenCompraModal');
    if (!modalElement) {
        mostrarAlerta('Error: Modal no encontrado', 'danger');
        return;
    }

    const resumenProductos = document.getElementById('resumenProductos');
    if (!resumenProductos) {
        mostrarAlerta('Error: Elemento resumen no encontrado', 'danger');
        return;
    }

    const totalPagado = document.getElementById('totalPagado');
    if (!totalPagado) {
        mostrarAlerta('Error: Elemento total no encontrado', 'danger');
        return;
    }

    // Obtener los datos del carrito actuales
    fetch('/carrito/datos')
        .then(response => response.json())
        .then(data => {
            if (!data.items || data.items.length === 0) {
                mostrarAlerta('El carrito está vacío', 'warning');
                return;
            }

            // Guardar los datos del resumen antes de procesar la compra
            resumenProductos.innerHTML = '';
            let total = 0;

            data.items.forEach(item => {
                const subtotal = item.producto.precio * item.cantidad;
                total += subtotal;

                const filaHTML = `
                    <tr>
                        <td>${item.producto.nombre}</td>
                        <td class="text-center">${item.cantidad}</td>
                        <td class="text-end">S/ ${item.producto.precio.toFixed(2)}</td>
                        <td class="text-end fw-bold">S/ ${subtotal.toFixed(2)}</td>
                    </tr>
                `;
                resumenProductos.insertAdjacentHTML('beforeend', filaHTML);
            });

            totalPagado.textContent = total.toFixed(2);

            // AHORA procesamos la compra

            return fetch('/carrito/finalizar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
        })
        .then(response => {
            if (!response) {
                throw new Error('No hay respuesta del servidor');
            }
            return response.json();
        })
        .then(result => {

            if (result.success) {

                // Cerrar offcanvas
                const offcanvasElement = document.getElementById('carritoOffcanvas');
                const offcanvas = bootstrap.Offcanvas.getInstance(offcanvasElement);
                if (offcanvas) {
                    offcanvas.hide();
                }

                // Mostrar modal de resumen
                const resumenModal = new bootstrap.Modal(document.getElementById('resumenCompraModal'));
                resumenModal.show();

                // Actualizar contador del carrito a 0
                actualizarContadorCarrito(0);

                // Recargar página cuando se cierre el modal
                const modalElement = document.getElementById('resumenCompraModal');
                modalElement.addEventListener('hidden.bs.modal', function () {
                    window.location.reload();
                }, { once: true });

            } else {
                mostrarAlerta(result.message || 'Error al procesar la compra', 'danger');
            }
        })
        .catch(error => {
            mostrarAlerta('Error al procesar la compra: ' + error.message, 'danger');
        });
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {

    // Cargar carrito al abrir el offcanvas
    const carritoOffcanvas = document.getElementById('carritoOffcanvas');
    if (carritoOffcanvas) {
        carritoOffcanvas.addEventListener('show.bs.offcanvas', function() {
            cargarCarrito();
        });
    }

    // Botón finalizar compra
    const btnFinalizarCompra = document.getElementById('btnFinalizarCompra');
    if (btnFinalizarCompra) {
        btnFinalizarCompra.addEventListener('click', function(e) {
            e.preventDefault();
            finalizarCompra();
        });
    }

    // Botones agregar al carrito
    const botonesAgregar = document.querySelectorAll('.agregar-carrito');
    botonesAgregar.forEach(boton => {
        boton.addEventListener('click', function() {
            const productoId = this.getAttribute('data-producto-id');
            agregarAlCarrito(productoId);
        });
    });

    // Cargar contador inicial si hay sesión
    if (verificarSesion()) {
        actualizarContadorInicial();
    }
});

// Función para actualizar contador inicial
function actualizarContadorInicial() {
    fetch('/carrito/datos')
        .then(response => response.json())
        .then(data => {
            if (data.items && Array.isArray(data.items)) {
                const totalItems = data.items.reduce((sum, item) => sum + item.cantidad, 0);
                actualizarContadorCarrito(totalItems);
            }
        });
}