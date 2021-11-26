package br.edu.ifsp.scl.ads.pdm.dados

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.dados.MainActivity.Constantes.CONFIGURACOES_ARQUIVO
import br.edu.ifsp.scl.ads.pdm.dados.MainActivity.Constantes.NUMERO_DADOS_ATRIBUTO
import br.edu.ifsp.scl.ads.pdm.dados.MainActivity.Constantes.NUMERO_FACES_ATRIBUTO
import br.edu.ifsp.scl.ads.pdm.dados.MainActivity.Constantes.VALOR_NAO_ENCONTRADO
import br.edu.ifsp.scl.ads.pdm.dados.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var geradorRandomico: Random

    private object Constantes {
        const val CONFIGURACOES_ARQUIVO = "configuracoes"
        const val NUMERO_DADOS_ATRIBUTO = "numeroDados"
        const val NUMERO_FACES_ATRIBUTO = "numeroFaces"
        const val VALOR_NAO_ENCONTRADO = -1
    }

    private lateinit var configuracoesSharedPreferences: SharedPreferences

    private lateinit var settingsActivityLauncher: ActivityResultLauncher<Intent>

    private var numeroDados: Int = Configuracao().numeroDados
    private var numeroFaces: Int = Configuracao().numeroFaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        geradorRandomico = Random(System.currentTimeMillis())

        configuracoesSharedPreferences = getSharedPreferences(CONFIGURACOES_ARQUIVO, MODE_PRIVATE)
        carregaConfiguracoes()

        activityMainBinding.jogarDadoBt.setOnClickListener {
            val resultado1: Int = geradorRandomico.nextInt(1..numeroFaces)
            var resultadoString = "A(s) face(s) sorteada(s) foi(ram) $resultado1"

            var resultado2 = 0
            if (numeroDados == 2) {
                resultado2 = geradorRandomico.nextInt(1..numeroFaces)
            }

            mostrarDado(resultado1, activityMainBinding.resultado1Iv, activityMainBinding.faceLl1)

            if (resultado2 != 0) {
                resultadoString = "$resultadoString e $resultado2"
                mostrarDado(resultado2, activityMainBinding.resultado2Iv, activityMainBinding.faceLl2)
            } else { activityMainBinding.faceLl2.visibility = View.GONE }

            resultadoString.also { activityMainBinding.resultadoTv.text = it }
        }

        settingsActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Modificações da minha View
                if (result.data != null) {
                    val configuracao: Configuracao? = result.data?.getParcelableExtra(Intent.EXTRA_USER)

                    // Exercício
                    if (configuracao != null) {
                        numeroDados = configuracao.numeroDados
                        numeroFaces = configuracao.numeroFaces
                    }
                }
            }
        }
    }

    private fun mostrarDado(resultado: Int, resultadoIv: ImageView, linearLayout: LinearLayout) {
        if (resultado <=6) {
            val nomeImagem = "dice_${resultado}"
            resultadoIv.setImageResource(
                resources.getIdentifier(nomeImagem, "mipmap", packageName)
            )
            linearLayout.visibility = View.VISIBLE
        } else { linearLayout.visibility = View.GONE }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsMi) {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            settingsActivityLauncher.launch(settingsIntent)
            return true
        }
        return false
    }

    private fun carregaConfiguracoes() {
        val numeroDados: Int = configuracoesSharedPreferences.getInt(NUMERO_DADOS_ATRIBUTO, VALOR_NAO_ENCONTRADO)
        val numeroFaces: Int = configuracoesSharedPreferences.getInt(NUMERO_FACES_ATRIBUTO, VALOR_NAO_ENCONTRADO)
        if (numeroDados != VALOR_NAO_ENCONTRADO) {
            this.numeroDados = numeroDados
        }
        if (numeroFaces != -1) {
            this.numeroFaces = numeroFaces
        }
    }
}