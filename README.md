# fundgrube

not a script i would show my mother-in-law and brag about it... but, ..., well, it does the job 😅 

## howto

after cloning this project, adjust the "url" to your needs, currently it will show results for MediaMarkt Mainz

1. install [babashka](https://github.com/babashka/babashka)
2. setup your environment 
   ```bash
   export FUNDGRUBE_TGRAM_API_KEY="1234567:ABCDEFGH-XYZ123"
   # you can find your channel id by logging into web.telegram.org
   # selecting your channel
   # don't forget to prepend 100 🤷‍♂️
   export FUNDGRUBE_TGRAM_CHANNEL="-100mychannelid"
   ```
3. `$ bb fundgrube.clj`
4. profit
5. bro down

## credits

inspired by: [RomanNess/fundgrube-crawler](https://github.com/RomanNess/fundgrube-crawler)
