package com.lugares_v.ui.lugar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares_v.AudioUtiles
import com.lugares_v.ImagenUtiles
import com.lugares_v.R
import com.lugares_v.databinding.FragmentAddLugarBinding
import com.lugares_v.databinding.FragmentLugarBinding
import com.lugares_v.model.Lugar
import com.lugares_v.viewmodel.LugarViewModel

class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel

    private lateinit var audioUtiles: AudioUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles


    private fun subeAudioNube() {
        val audioFile = audioUtiles.audioFile
        if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
            val ruta = Uri.fromFile(audioFile)

            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
            referencia.putFile(ruta)
                .addOnSuccessListener {
                    referencia.downloadUrl
                        .addOnSuccessListener {
                            val rutaAudio = it.toString()
                            subeImagenNube(rutaAudio)
                        }
                }
                .addOnFailureListener { subeImagenNube("") }
        } else {
            subeImagenNube("")
        }
    }

    private fun subeImagenNube(rutaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)
        val imagenFile = imagenUtiles.imagenFile
        if (imagenFile.exists() && imagenFile.isFile && imagenFile.canRead()) {
            val ruta = Uri.fromFile(imagenFile)
            val rutaNube =
                "lugaresApp/${Firebase.auth.currentUser?.uid.toString()}/imagenes/${imagenFile.name}"
            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)
            referencia.putFile(ruta)
                .addOnSuccessListener {
                    referencia.downloadUrl
                        .addOnSuccessListener {
                            val rutaImagen = it.toString()
                            agregarLugar(rutaAudio, rutaImagen)
                        }
                }
                .addOnFailureListener {
                    agregarLugar(rutaAudio, "")
                }
        } else {
            agregarLugar(rutaAudio, "")
        }
    }

    private fun agregarLugar(rutaAudio: String, rutaImagen: String) {
        val nombre = binding.etNombre.text.toString()
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val web = binding.etWeb.text.toString()
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()

            val lugar = Lugar("", nombre, correo, telefono, web,
                latitud, longitud, altura,rutaAudio, rutaImagen)

            lugarViewModel.addLugar(lugar)

            Toast.makeText(requireContext(),getString(R.string.msg_lugar_add),Toast.LENGTH_SHORT).show()

            binding.progressBar.visibility = ProgressBar.INVISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.INVISIBLE

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAddLugar.setOnClickListener {
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            subeAudioNube()
        }


        binding.btAgregar.setOnClickListener {
            addLugar()
        }

        audioUtiles = AudioUtiles(
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio),
        )

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = ImagenUtiles(requireContext(),
            binding.btPhoto,binding.btRotaL,binding.btRotaR,binding.imagen,
            tomarFotoActivity)

        ubicaGPS()

        return binding.root
    }


    private fun ubicaGPS() {
        val ubicacion: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {  //Si no tengo los permisos entonces los solicito
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),105)
        } else {  //Si se tienen los permisos
            ubicacion.lastLocation.addOnSuccessListener { location: Location? ->
                if (location!=null) {
                    binding.tvLatitud.text ="${location.latitude}"
                    binding.tvLongitud.text ="${location.longitude}"
                    binding.tvAltura.text ="${location.altitude}"
                } else {
                    binding.tvLatitud.text ="0.00"
                    binding.tvLongitud.text ="0.00"
                    binding.tvAltura.text ="0.00"
                }
            }
        }

    }

    private fun addLugar() {
        val nombre=binding.etNombre.text.toString()
        val correo=binding.etCorreo.text.toString()
        val telefono=binding.etTelefono.text.toString()
        val web=binding.etWeb.text.toString()
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud= binding.tvLongitud.text.toString().toDouble()
        val altura = binding.tvAltura.text.toString().toDouble()

        if (nombre.isNotEmpty()) { //Si puedo crear un lugar
            val lugar= Lugar("",nombre,correo,telefono,web,latitud,
                longitud,altura,"","")
            lugarViewModel.addLugar(lugar)
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_added),Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {  //Mensaje de error...
            Toast.makeText(requireContext(),getString(R.string.msg_data),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}