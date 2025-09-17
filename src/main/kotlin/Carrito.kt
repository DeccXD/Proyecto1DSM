import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Carrito {
    private val items = mutableListOf<Pair<Producto, Int>>() // Producto + Cantidad

    fun agregarProducto(producto: Producto, cantidad: Int) {

        items.add(producto to cantidad)
    }

    // Mostrar carrito
    fun mostrarCarrito() {
        if (items.isEmpty()) {
            println("El carrito está vacío.")
            return
        }

        while (true) {
    // Mostrar lista de productos
            println("\n----- CARRITO DE COMPRAS -----")
            println(String.format("%-3s %-30s %-10s %-6s", "ID", "Nombre", "Precio", "Cant."))
            println("------------------------------------------------")
            for ((i, item) in items.withIndex()) {
                val (producto, cantidad) = item
                println(
                    String.format(
                        "%-3d %-30s %-10s %-6d",
                        i + 1,
                        producto.nombre,
                        "$${"%.2f".format(producto.precio)}",
                        cantidad
                    )
                )
            }

            val subtotal = items.sumOf { it.first.precio * it.second }
            val iva = subtotal * 0.13
            val totalConIVA = subtotal + iva

            println("------------------------------------------------")
            println("Subtotal: $${"%.2f".format(subtotal)}")
            println("IVA (13%): $${"%.2f".format(iva)}")
            println("TOTAL (con IVA): $${"%.2f".format(totalConIVA)}")

    // Menú de acciones
            println("\n¿Qué deseas hacer?")
            println("1) Imprimir recibo")
            println("2) Modificar/eliminar productos")
            println("3) Volver al menú principal")
            print("Opción: ")
            when (readLine()?.trim()) {
                "1" -> {
                    generarFactura()
                    return
                }
                "2" -> modificarProductoNoRecursivo()
                "3" -> return
                else -> println("ERROR Opción inválida, elige 1, 2 o 3.")
            }
        }
    }

    // Modificar o eliminar productos
    private fun modificarProductoNoRecursivo() {
        if (items.isEmpty()) {
            println("El carrito está vacío, no hay productos para modificar.")
            return
        }

        print("\nIngresa el ID del producto que deseas modificar (o 0 para cancelar): ")
        val id = readLine()?.toIntOrNull() ?: return

        if (id <= 0 || id > items.size) {
            println("ID inválido, operación cancelada.")
            return
        }

        val (producto, cantidadActual) = items[id - 1]

        print("Ingresa la nueva cantidad para '${producto.nombre}' (0 para eliminar): ")
        val nuevaCantidad = readLine()?.toIntOrNull() ?: return

        if (nuevaCantidad < 0) {
            println("Cantidad inválida.")
            return
        }

        when {
            nuevaCantidad == 0 -> {
                producto.cantidadDisponible += cantidadActual
                items.removeAt(id - 1)
                println("'${producto.nombre}' eliminado del carrito.")
            }
            nuevaCantidad > cantidadActual -> {
                val extra = nuevaCantidad - cantidadActual
                if (extra > producto.cantidadDisponible) {
                    println("Lo sentimos, no hay suficiente stock. Solo quedan ${producto.cantidadDisponible}.")
                    return
                }
                producto.cantidadDisponible -= extra
                items[id - 1] = producto to nuevaCantidad
                println("Cantidad actualizada a $nuevaCantidad unidades de '${producto.nombre}'.")
            }
            nuevaCantidad < cantidadActual -> {
                val diferencia = cantidadActual - nuevaCantidad
                producto.cantidadDisponible += diferencia
                items[id - 1] = producto to nuevaCantidad
                println("Cantidad reducida a $nuevaCantidad unidades de '${producto.nombre}'.")
            }
            else -> println("La cantidad no cambió.")
        }
    }

    fun generarFactura() {
        if (items.isEmpty()) {
            println("No se puede facturar, el carrito está vacío.")
            return
        }

        val dir = File("facturas")
        if (!dir.exists()) dir.mkdirs()

        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val archivo = File(dir, "factura-$fecha.txt")

        var subtotal = 0.0
        val builder = StringBuilder()
        builder.appendLine("===== FACTURA DE COMPRA =====")
        builder.appendLine("Fecha: $fecha")
        builder.appendLine("-----------------------------")
        builder.appendLine(String.format("%-30s %-10s %-6s %-10s", "Producto", "Precio", "Cant.", "Total"))

        for ((producto, cantidad) in items) {
            val totalProducto = producto.precio * cantidad
            builder.appendLine(
                String.format(
                    "%-30s %-10s %-6d %-10s",
                    producto.nombre,
                    "$${"%.2f".format(producto.precio)}",
                    cantidad,
                    "$${"%.2f".format(totalProducto)}"
                )
            )
            subtotal += totalProducto
        }

        val iva = subtotal * 0.13
        val totalConIVA = subtotal + iva

        builder.appendLine("-----------------------------")
        builder.appendLine("Subtotal: $${"%.2f".format(subtotal)}")
        builder.appendLine("IVA (13%): $${"%.2f".format(iva)}")
        builder.appendLine("TOTAL: $${"%.2f".format(totalConIVA)}")
        builder.appendLine("=============================")

        archivo.writeText(builder.toString())

        println("Factura generada en: ${archivo.absolutePath}")

        items.clear()
    }
}
