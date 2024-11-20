package ph.edu.auf.realmdiscussion.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.components.ItemPet
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetScreen(petViewModel: PetViewModel = viewModel()){

    val pets by petViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState()}
    val coroutineScope = rememberCoroutineScope()

    var snackbarShown by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    val filteredPets = pets.filter { it.name.contains(searchQuery, ignoreCase = true) }

    var newPetName by remember { mutableStateOf("") }

    var hasOwner by remember { mutableStateOf(false) }
    var ownerName by remember { mutableStateOf("") }

    val petTypes = listOf("Dog", "Cat", "Bird", "Fish")
    var selectedPetType by remember { mutableStateOf(petTypes.first()) }

    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(petViewModel.showSnackbar)
    {
        petViewModel.showSnackbar.collect{ message ->
            if(!snackbarShown){
                snackbarShown = true
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                    when(result){
                        SnackbarResult.Dismissed ->{
                            Log.d("PetScreen", "Dismissed")
                            snackbarShown = false
                        }
                        SnackbarResult.ActionPerformed ->{
                            Log.d("PetScreen", "Action performed")
                            snackbarShown = false
                        }
                    }
                }
            }
        }
    }



    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Pets") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        TextField(value = newPetName, onValueChange = { newPetName = it }, label = { Text("Pet Name") })

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedPetType,
                onValueChange = { },
                label = { Text("Pet Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
        }

        Button(onClick = { petViewModel.addPet(newPetName) }) {
            Text("Add Pet")
        }

        Row {
            Checkbox(checked = hasOwner, onCheckedChange = { hasOwner = it })
            Text("Has Owner")
        }

        if (hasOwner) {
            TextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Owner Name") })
        }


        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                itemsIndexed(
                    items = filteredPets,
                    key = { _, item -> item.id }
                ) { _, petContent ->
                    ItemPet(petContent, onRemove = petViewModel::deletePet)
                }
            }
        }
    }



}

@Preview(showBackground = true)
@Composable
fun PetScreenPreview(){
    PetScreen()
}

