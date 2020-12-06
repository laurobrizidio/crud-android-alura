package br.com.alura.estoque.ui.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import br.com.alura.estoque.R;
import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private ListaProdutosAdapter adapter;
    private ProdutoDAO dao;
    private ProdutoRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        EstoqueDatabase db = EstoqueDatabase.getInstance(this);
        dao = db.getProdutoDAO();
        repo = new ProdutoRepository(dao);
        repo.buscaProdutos(adapter::atualiza);
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao, Produto produtoRemovido) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produtoRemovido);
            return null;
        }, resultado -> adapter.remove(posicao))
                .execute();
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this,
                produto -> repo.salva(produto, produtoSalvo -> {
                    adapter.adiciona(produtoSalvo);
                })).mostra();
    }


    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoEditado -> edita(posicao, produtoEditado))
                .mostra();
    }

    private void edita(int posicao, Produto produto) {
        repo.update(produto, produtos -> {
            adapter.edita(posicao, produtos);
        });

    }


}