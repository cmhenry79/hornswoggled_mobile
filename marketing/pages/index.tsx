import Head from 'next/head'

export default function Home() {
  return (
    <>
      <Head>
        <title>Hornswoggled - The Ultimate Party Game</title>
        <meta name="description" content="Play the funniest party game with friends. Create hilarious definitions, compete with AI bots, and laugh until you cry!" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main className="min-h-screen bg-gradient-to-br from-hornswoggled-purple via-hornswoggled-pink to-hornswoggled-orange">
        {/* Hero Section */}
        <section className="container mx-auto px-6 py-20 text-white">
          <div className="text-center">
            <div className="text-8xl mb-6">🎮</div>
            <h1 className="text-6xl md:text-8xl font-bold mb-6">
              HORNSWOGGLED
            </h1>
            <p className="text-2xl md:text-3xl mb-8 opacity-90">
              The Ultimate Party Game
            </p>
            <p className="text-xl mb-12 max-w-2xl mx-auto">
              Create hilarious definitions, compete with AI-powered bots, and discover who's the funniest in your friend group!
            </p>

            <div className="flex flex-col md:flex-row gap-4 justify-center">
              <a
                href="#download"
                className="bg-white text-hornswoggled-purple px-8 py-4 rounded-full text-xl font-bold hover:scale-105 transition-transform"
              >
                Download Now
              </a>
              <a
                href="#features"
                className="border-2 border-white text-white px-8 py-4 rounded-full text-xl font-bold hover:bg-white hover:text-hornswoggled-purple transition-colors"
              >
                Learn More
              </a>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section id="features" className="bg-white py-20">
          <div className="container mx-auto px-6">
            <h2 className="text-5xl font-bold text-center mb-16 text-hornswoggled-purple">
              Why You'll Love It
            </h2>

            <div className="grid md:grid-cols-3 gap-8">
              <FeatureCard
                emoji="🤖"
                title="AI-Powered Bots"
                description="Play with hilarious AI bots powered by Gemini - each with unique personalities and humor styles!"
              />
              <FeatureCard
                emoji="😂"
                title="Multiple Submission Types"
                description="Text, GIFs, images, doodles, and emoji mashups - express yourself however you want!"
              />
              <FeatureCard
                emoji="🎨"
                title="Customization"
                description="Unlock cosmetics, word packs, and more. Make the game truly yours!"
              />
              <FeatureCard
                emoji="🏆"
                title="Competitive & Casual"
                description="Climb leaderboards or just play for laughs. Track stats or ignore them!"
              />
              <FeatureCard
                emoji="🌐"
                title="Real-time Multiplayer"
                description="Play with friends anywhere. Public or private rooms, your choice!"
              />
              <FeatureCard
                emoji="⚡"
                title="Fast & Fun"
                description="Quick rounds, instant laughs. Perfect for parties or killing time!"
              />
            </div>
          </div>
        </section>

        {/* How to Play */}
        <section className="bg-gradient-to-r from-hornswoggled-blue to-hornswoggled-green py-20 text-white">
          <div className="container mx-auto px-6">
            <h2 className="text-5xl font-bold text-center mb-16">
              How to Play
            </h2>

            <div className="max-w-3xl mx-auto space-y-8">
              <Step number={1} title="Create or Join a Room">
                Start a game with friends or jump into a public room
              </Step>
              <Step number={2} title="Get a Word">
                Each round, you'll get a random word to define
              </Step>
              <Step number={3} title="Submit Your Answer">
                Create the funniest definition using text, GIFs, images, or doodles
              </Step>
              <Step number={4} title="Vote & Win">
                The judge picks the best answer. Winners get points and glory!
              </Step>
            </div>
          </div>
        </section>

        {/* Download Section */}
        <section id="download" className="bg-white py-20">
          <div className="container mx-auto px-6 text-center">
            <h2 className="text-5xl font-bold mb-8 text-hornswoggled-purple">
              Ready to Play?
            </h2>
            <p className="text-xl mb-12 max-w-2xl mx-auto text-gray-700">
              Download Hornswoggled now and start laughing with friends!
            </p>

            <div className="flex flex-col md:flex-row gap-6 justify-center">
              <a
                href="https://play.google.com/store"
                className="bg-black text-white px-8 py-4 rounded-lg text-xl font-semibold hover:scale-105 transition-transform flex items-center justify-center gap-3"
              >
                <svg className="w-8 h-8" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M3,20.5V3.5C3,2.91 3.34,2.39 3.84,2.15L13.69,12L3.84,21.85C3.34,21.6 3,21.09 3,20.5M16.81,15.12L6.05,21.34L14.54,12.85L16.81,15.12M20.16,10.81C20.5,11.08 20.75,11.5 20.75,12C20.75,12.5 20.5,12.92 20.16,13.19L17.89,14.5L15.39,12L17.89,9.5L20.16,10.81M6.05,2.66L16.81,8.88L14.54,11.15L6.05,2.66Z" />
                </svg>
                Get it on Google Play
              </a>

              <a
                href="https://apps.apple.com"
                className="bg-black text-white px-8 py-4 rounded-lg text-xl font-semibold hover:scale-105 transition-transform flex items-center justify-center gap-3"
              >
                <svg className="w-8 h-8" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M18.71,19.5C17.88,20.74 17,21.95 15.66,21.97C14.32,22 13.89,21.18 12.37,21.18C10.84,21.18 10.37,21.95 9.1,22C7.79,22.05 6.8,20.68 5.96,19.47C4.25,17 2.94,12.45 4.7,9.39C5.57,7.87 7.13,6.91 8.82,6.88C10.1,6.86 11.32,7.75 12.11,7.75C12.89,7.75 14.37,6.68 15.92,6.84C16.57,6.87 18.39,7.1 19.56,8.82C19.47,8.88 17.39,10.1 17.41,12.63C17.44,15.65 20.06,16.66 20.09,16.67C20.06,16.74 19.67,18.11 18.71,19.5M13,3.5C13.73,2.67 14.94,2.04 15.94,2C16.07,3.17 15.6,4.35 14.9,5.19C14.21,6.04 13.07,6.7 11.95,6.61C11.8,5.46 12.36,4.26 13,3.5Z" />
                </svg>
                Download on App Store
              </a>
            </div>
          </div>
        </section>

        {/* Footer */}
        <footer className="bg-gray-900 text-white py-12">
          <div className="container mx-auto px-6 text-center">
            <p className="text-xl font-bold mb-4">HORNSWOGGLED</p>
            <p className="text-gray-400 mb-6">
              © 2025 Hornswoggled. All rights reserved.
            </p>
            <div className="flex justify-center gap-6">
              <a href="/privacy" className="hover:text-hornswoggled-pink">Privacy Policy</a>
              <a href="/terms" className="hover:text-hornswoggled-pink">Terms of Service</a>
              <a href="/support" className="hover:text-hornswoggled-pink">Support</a>
            </div>
          </div>
        </footer>
      </main>
    </>
  )
}

function FeatureCard({ emoji, title, description }: { emoji: string; title: string; description: string }) {
  return (
    <div className="bg-gray-50 p-8 rounded-2xl hover:shadow-xl transition-shadow">
      <div className="text-6xl mb-4">{emoji}</div>
      <h3 className="text-2xl font-bold mb-3 text-hornswoggled-purple">{title}</h3>
      <p className="text-gray-700">{description}</p>
    </div>
  )
}

function Step({ number, title, children }: { number: number; title: string; children: React.ReactNode }) {
  return (
    <div className="flex gap-6 items-start">
      <div className="bg-white text-hornswoggled-blue text-3xl font-bold rounded-full w-16 h-16 flex items-center justify-center flex-shrink-0">
        {number}
      </div>
      <div>
        <h3 className="text-2xl font-bold mb-2">{title}</h3>
        <p className="text-lg opacity-90">{children}</p>
      </div>
    </div>
  )
}
