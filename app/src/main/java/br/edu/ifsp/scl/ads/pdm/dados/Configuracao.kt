package br.edu.ifsp.scl.ads.pdm.dados

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Configuracao(val numeroDados: Int = 1, val numeroFaces: Int = 6): Parcelable
