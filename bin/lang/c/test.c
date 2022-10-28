// 識別子がうまく切り出せるか、１文字記号の字句が消えていないか、確認

//abc+-def[ghij+*k]lmn
// abc + - def [ ghij + * k ] lmn     のはず。 + あたりが消えてなくなっていないか？

//ab0c+-1def[gh2ij+*k3]4lm5n
// ab0c + - 1 def [ gh2ij + * k3 ] 4 lm5n　　　のはず。
// + ] が消えてなくなっていないか？　lm5n の l が消えてなくなっていないか？


//i_a ip_a ia_a ipa_a
// _ が英字の扱いを受けているか？