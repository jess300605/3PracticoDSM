package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ResourceItem

@Composable
fun ResourceFormDialog(
    initialItem: ResourceItem? = null,
    onDismiss: () -> Unit,
    onSave: (ResourceItem, Boolean) -> Unit
) {
    val isEdit = initialItem != null

    var titulo by remember { mutableStateOf(initialItem?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(initialItem?.descripcion ?: "") }
    var tipo by remember { mutableStateOf(initialItem?.tipo ?: "Libro") }
    var enlace by remember { mutableStateOf(initialItem?.enlace ?: "") }
    var imagen by remember { mutableStateOf(initialItem?.imagen ?: "") }

    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val resourceTypes = listOf("Libro", "Video", "Artículo", "Tutorial")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isEdit) Icons.Filled.Edit else Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(if (isEdit) R.string.edit_resource_title else R.string.add_resource_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it; errorMessage = null },
                    label = { Text(stringResource(R.string.field_title_label)) },
                    placeholder = { Text(stringResource(R.string.field_title_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_resource_title")
                )

                // Tipo selector
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { typeDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_select_type")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${stringResource(R.string.field_type_label)}: $tipo")
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        resourceTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    tipo = t
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it; errorMessage = null },
                    label = { Text(stringResource(R.string.field_description_label)) },
                    placeholder = { Text(stringResource(R.string.field_description_placeholder)) },
                    maxLines = 4,
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_resource_desc")
                )

                // Enlace / URL
                OutlinedTextField(
                    value = enlace,
                    onValueChange = { enlace = it; errorMessage = null },
                    label = { Text(stringResource(R.string.field_url_label)) },
                    placeholder = { Text(stringResource(R.string.field_url_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_resource_url")
                )

                // Imagen URL
                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = { Text(stringResource(R.string.field_image_label)) },
                    placeholder = { Text(stringResource(R.string.field_image_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Image, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_resource_img")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isBlank() || descripcion.isBlank() || enlace.isBlank()) {
                        errorMessage = "Por favor completa título, descripción y enlace."
                        return@Button
                    }
                    if (!enlace.startsWith("http://") && !enlace.startsWith("https://")) {
                        errorMessage = "El enlace debe comenzar con http:// o https://"
                        return@Button
                    }

                    val finalImage = if (imagen.isBlank()) {
                        when (tipo.lowercase()) {
                            "libro" -> "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80"
                            "video" -> "https://images.unsplash.com/photo-1618401471353-b98aedd04e11?w=600&auto=format&fit=crop&q=80"
                            "artículo", "articulo" -> "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=600&auto=format&fit=crop&q=80"
                            else -> "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80"
                        }
                    } else imagen.trim()

                    val savedItem = ResourceItem(
                        id = initialItem?.id ?: "",
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        tipo = tipo,
                        enlace = enlace.trim(),
                        imagen = finalImage,
                        calificacion = initialItem?.calificacion ?: 5.0,
                        totalVotos = initialItem?.totalVotos ?: 1
                    )

                    onSave(savedItem, isEdit)
                },
                modifier = Modifier.testTag("btn_save_resource_form")
            ) {
                Text(stringResource(if (isEdit) R.string.btn_update else R.string.btn_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}
