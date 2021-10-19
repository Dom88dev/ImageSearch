package dom.project.imagesearch.di

import dom.project.imagesearch.repository.Repository
import dom.project.imagesearch.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { Repository(get(), dao = get()) }
    viewModel { MainViewModel(get()) }
}