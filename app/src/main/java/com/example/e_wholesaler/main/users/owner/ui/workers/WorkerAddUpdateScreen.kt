package com.example.e_wholesaler.main.users.owner.ui.workers

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.auth.utils.Gender
import com.example.e_wholesaler.main.users.owner.dtos.Worker

@Preview(showBackground = true)
@Composable
fun WorkerFormScreenAddPreview() {
    WorkerAddUpdateScreen(
        worker = null,
        shopId = 101,
        onBackClicked = {},
        onSaveClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun WorkerFormScreenEditPreview() {
    val sampleWorker = Worker(
        id = 1,
        name = "Rajesh Kumar",
        gender = Gender.MALE,
        mobNo = "9876543210",
        address = "123, Street Name, Area",
        city = "Pune",
        state = "Maharashtra",
        shopId = 101,
        salary = 25000.0
    )
    WorkerAddUpdateScreen(
        worker = sampleWorker,
        shopId = 101,
        onBackClicked = {},
        onSaveClicked = {}
    )
}

/**
 * A unified screen for adding and updating worker details.
 * If [worker] is null, the screen operates in "Add Mode".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerAddUpdateScreen(
    worker: Worker?,
    shopId: Long,
    onBackClicked: () -> Unit,
    onSaveClicked: (Worker) -> Unit
) {
    val isEditMode = worker != null

    // State initialization
    var name by remember(worker) { mutableStateOf(worker?.name ?: "") }
    var mobNo by remember(worker) { mutableStateOf(worker?.mobNo ?: "") }
    var address by remember(worker) { mutableStateOf(worker?.address ?: "") }
    var city by remember(worker) { mutableStateOf(worker?.city ?: "") }
    var state by remember(worker) { mutableStateOf(worker?.state ?: "") }
    var salaryStr by remember(worker) { mutableStateOf(worker?.salary?.toString() ?: "") }
    var gender by remember(worker) { mutableStateOf(worker?.gender ?: Gender.MALE) }

    var genderExpanded by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    // Logic for changes and validity
    val hasChanges by remember(worker, name, mobNo, address, city, state, salaryStr, gender) {
        derivedStateOf {
            if (isEditMode) {
                name != worker.name || mobNo != worker.mobNo || address != worker.address ||
                        city != worker.city || state != worker.state ||
                        salaryStr != worker.salary.toString() || gender != worker.gender
            } else {
                name.isNotBlank() || mobNo.isNotBlank() || address.isNotBlank() ||
                        city.isNotBlank() || state.isNotBlank() || salaryStr.isNotBlank()
            }
        }
    }

    val isFormValid by remember(name, mobNo, salaryStr) {
        derivedStateOf {
            name.isNotBlank() && mobNo.length >= 10 && salaryStr.toDoubleOrNull() != null
        }
    }

    BackHandler {
        if (hasChanges) showBackDialog = true else onBackClicked()
    }

    // Dialogs
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = if (isEditMode) "Confirm Update" else "Confirm Addition",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isEditMode) "Are you sure you want to save these changes?"
                    else "Are you sure you want to add ${name.trim()} as a new worker?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        onSaveClicked(
                            Worker(
                                id = worker?.id ?: 0L, // 0L for new workers
                                name = name.trim(),
                                mobNo = mobNo.trim(),
                                address = address.trim(),
                                city = city.trim(),
                                state = state.trim(),
                                salary = salaryStr.toDoubleOrNull() ?: 0.0,
                                gender = gender,
                                shopId = shopId
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
                ) { Text("Confirm") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Discard Changes?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = if (isEditMode) "You have unsaved changes. Do you want to discard them?"
                    else "You have entered some details. Do you want to discard them and go back?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBackDialog = false
                        onBackClicked()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                ) { Text("Discard") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBackDialog = false }) { Text("Keep Editing") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Worker" else "Add Worker",
                        color = IconColorWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) showBackDialog = true else onBackClicked()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = IconColorWhite)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSaveDialog = true },
                        enabled = isFormValid && (if (isEditMode) hasChanges else true)
                    ) {
                        Icon(
                            Icons.Default.Check, "Save",
                            tint = if (isFormValid && (if (isEditMode) hasChanges else true)) IconColorWhite else IconColorWhite.copy(
                                alpha = 0.4f
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarBlue)
            )
        },
        containerColor = BackgroundScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeader("Personal Details")

            UpdateTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Worker Name"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Gender")
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        OutlinedTextField(
                            value = gender.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardBackgroundWhite,
                                unfocusedContainerColor = CardBackgroundWhite,
                                focusedBorderColor = TopBarBlue,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            Gender.entries.forEach { genderOption ->
                                DropdownMenuItem(
                                    text = { Text(genderOption.name) },
                                    onClick = {
                                        gender = genderOption
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                UpdateTextField(
                    value = mobNo,
                    onValueChange = { if (it.length <= 10) mobNo = it },
                    label = "Mobile No",
                    placeholder = "10 Digit",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.weight(1.2f)
                )
            }

            SectionHeader("Employment & Location")

            UpdateTextField(
                value = salaryStr,
                onValueChange = { salaryStr = it },
                label = "Monthly Salary",
                placeholder = "0.00",
                keyboardType = KeyboardType.Number
            )

            UpdateTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                placeholder = "Residential Address",
                singleLine = false,
                minLines = 2
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                UpdateTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City",
                    placeholder = "City Name",
                    modifier = Modifier.weight(1f)
                )
                UpdateTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = "State",
                    placeholder = "State Name",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TopBarBlue,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextSecondary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun UpdateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackgroundWhite,
                unfocusedContainerColor = CardBackgroundWhite,
                focusedBorderColor = TopBarBlue,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = TopBarBlue
            )
        )
    }
}
